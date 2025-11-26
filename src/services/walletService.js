import { walletAPI } from './api'

// 减少i18n依赖，直接使用简单错误消息
const ERROR_MESSAGES = {
  walletCreationFailed: 'Wallet creation failed',
  walletNotFound: 'Wallet not found',
  transferFailed: 'Transfer failed'
}

class WalletService {
  constructor(dispatch) {
    this.dispatch = dispatch
    // 缓存当前钱包ID，避免重复从localStorage读取
    this.currentWalletId = null
  }

  // 通用加载包装器，减少重复代码
  async _withLoading(action) {
    try {
      this.dispatch({ type: 'SET_LOADING', payload: true })
      return await action()
    } catch (error) {
      this.dispatch({ type: 'SET_ERROR', payload: error.message })
      throw error
    } finally {
      this.dispatch({ type: 'SET_LOADING', payload: false })
    }
  }

  async createWallet(username, initialBalance) {
    return this._withLoading(async () => {
      const result = await walletAPI.createWallet(username, initialBalance)
      
      if (result.success) {
        const wallet = result.wallet
        this.currentWalletId = wallet.id
        this.dispatch({ type: 'SET_WALLET', payload: wallet })
        localStorage.setItem('wallet', JSON.stringify(wallet))
        return result
      } else {
        throw new Error(ERROR_MESSAGES.walletCreationFailed)
      }
    })
  }

  async loginWallet(username) {
    return this._withLoading(async () => {
      const result = await walletAPI.getWalletByUsername(username)
      
      if (result.success) {
        const wallet = result.wallet
        this.currentWalletId = wallet.id
        this.dispatch({ type: 'SET_WALLET', payload: wallet })
        localStorage.setItem('wallet', JSON.stringify(wallet))
        return result
      } else {
        throw new Error(ERROR_MESSAGES.walletNotFound)
      }
    })
  }

  async getWallet(walletId) {
    return this._withLoading(async () => {
      const result = await walletAPI.getWallet(walletId)
      this.currentWalletId = walletId
      this.dispatch({ type: 'SET_WALLET', payload: result.wallet })
      return result
    })
  }

  async transfer(fromWalletId, toWalletId, amount) {
    return this._withLoading(async () => {
      const result = await walletAPI.transfer(fromWalletId, toWalletId, amount)
      
      if (result.success) {
        // 只有当转出账户是当前账户时才更新状态
        if (fromWalletId === this.currentWalletId) {
          // 避免重复的API调用，直接更新本地缓存的余额
          const currentWallet = JSON.parse(localStorage.getItem('wallet'))
          if (currentWallet && currentWallet.id === fromWalletId) {
            // 假设API返回了更新后的余额
            if (result.updatedWallet) {
              this.dispatch({ type: 'SET_WALLET', payload: result.updatedWallet })
              localStorage.setItem('wallet', JSON.stringify(result.updatedWallet))
            }
          }
        }
        return result
      } else {
        throw new Error(ERROR_MESSAGES.transferFailed)
      }
    })
  }

  async transferByUsername(fromUsername, toUsername, amount) {
    return this._withLoading(async () => {
      const result = await walletAPI.transferByUsername(fromUsername, toUsername, amount)
      
      if (result.success && this.currentWalletId) {
        // 只有当有当前钱包ID时才更新状态
        // 避免不必要的localStorage读取和API调用
        if (result.updatedWallet) {
          this.dispatch({ type: 'SET_WALLET', payload: result.updatedWallet })
          localStorage.setItem('wallet', JSON.stringify(result.updatedWallet))
        }
      }
      
      if (!result.success) {
        throw new Error(result.error || ERROR_MESSAGES.transferFailed)
      }
      
      return result
    })
  }

  async getTransactionHistory(walletId, page = 1, limit = 10) {
    return this._withLoading(async () => {
      const result = await walletAPI.getTransactionHistory(walletId, page, limit)
      
      // 简化分页数据处理，避免不必要的计算
      const paginationData = result.pagination || {
        currentPage: result.page || page,
        totalPages: result.totalPages || 1,
        totalTransactions: result.total || 0,
        limit: limit,
        hasNextPage: result.hasNextPage || false,
        hasPreviousPage: result.hasPreviousPage || page > 1
      }
      
      // 减少状态更新次数，只在数据变化时更新
      this.dispatch({ type: 'SET_TRANSACTIONS', payload: result.transactions || [] })
      this.dispatch({ type: 'SET_PAGINATION', payload: paginationData })
      
      return result
    })
  }

  // 简单的错误清除方法
  clearError() {
    this.dispatch({ type: 'CLEAR_ERROR' })
  }
}

export default WalletService