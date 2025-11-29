import axios from 'axios'

// 简化的错误消息，减少i18n依赖
const ERROR_MESSAGES = {
  resourceNotFound: 'Resource not found',
  authenticationRequired: 'Authentication required',
  accessDenied: 'Access denied',
  serverError: 'Server error'
}

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:3200/api'

// 配置axios实例，减少默认头部设置以降低请求大小
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 8000, // 略微降低超时时间
  // 移除默认Content-Type，让axios自动处理
  headers: {},
})

// 简化的请求拦截器，移除开发日志
api.interceptors.request.use(
  (config) => {
    // 只在需要时添加Content-Type，避免重复设置
    if (!config.headers['Content-Type'] && config.method !== 'get' && config.method !== 'delete') {
      config.headers['Content-Type'] = 'application/json'
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 简化的响应拦截器，移除不必要的错误对象创建
api.interceptors.response.use(
  (response) => response.data, // 直接返回响应数据，减少一层嵌套
  (error) => {
    // 移除开发环境日志
    
    // 简化错误消息提取
    let errorMessage = error.message
    if (error.response && error.response.data) {
      if (typeof error.response.data === 'string') {
        errorMessage = error.response.data
      } else if (error.response.data.error || error.response.data.message) {
        errorMessage = error.response.data.error || error.response.data.message
      } else {
        // 根据状态码使用预定义的错误消息
        const statusMessage = ERROR_MESSAGES[`status${error.response.status}`]
        if (statusMessage) {
          errorMessage = statusMessage
        } else if (error.response.status >= 500) {
          errorMessage = ERROR_MESSAGES.serverError
        }
      }
    }
    
    // 直接修改原始错误对象，避免创建新对象
    error.message = errorMessage
    error.status = error.response?.status
    
    return Promise.reject(error)
  }
)

// 简单的缓存机制，用于不经常变化的数据
const cache = new Map()
const CACHE_DURATION = 30000 // 30秒缓存

function getCachedData(key) {
  const cached = cache.get(key)
  if (cached && Date.now() - cached.timestamp < CACHE_DURATION) {
    return cached.data
  }
  cache.delete(key)
  return null
}

function setCachedData(key, data) {
  cache.set(key, { data, timestamp: Date.now() })
}

// 优化的Wallet API方法
export const walletAPI = {
  // 创建钱包
  createWallet: async (username, initialBalance) => {
    // 清除可能的缓存
    cache.delete(`wallet_username_${username}`)
    return api.post('/wallets', { username, initialBalance })
  },

  // 获取钱包信息（添加简单缓存）
  getWallet: async (walletId) => {
    const cacheKey = `wallet_${walletId}`
    const cached = getCachedData(cacheKey)
    if (cached) return cached
    
    const data = await api.get(`/wallets/${walletId}`)
    setCachedData(cacheKey, data)
    return data
  },

  // 根据用户名获取钱包（添加缓存）
  getWalletByUsername: async (username) => {
    const cacheKey = `wallet_username_${username}`
    const cached = getCachedData(cacheKey)
    if (cached) return cached
    
    const data = await api.get(`/wallets/username/${username}`)
    setCachedData(cacheKey, data)
    return data
  },

  // 更新钱包余额（清除相关缓存）
  updateBalance: async (walletId, amount) => {
    cache.delete(`wallet_${walletId}`)
    return api.put(`/wallets/${walletId}/balance`, { amount })
  },

  // 执行转账
  transfer: async (fromWalletId, toWalletId, amount) => {
    // 清除相关缓存
    cache.delete(`wallet_${fromWalletId}`)
    cache.delete(`wallet_${toWalletId}`)
    // 清除交易历史缓存
    cache.delete(`transactions_${fromWalletId}`)
    cache.delete(`transactions_${toWalletId}`)
    
    return api.post('/transfers', { fromWalletId, toWalletId, amount })
  },

  // 根据用户名执行转账
  transferByUsername: async (fromUsername, toUsername, amount) => {
    // 清除相关缓存
    cache.delete(`wallet_username_${fromUsername}`)
    cache.delete(`wallet_username_${toUsername}`)
    
    return api.post('/transfers/by-username', { fromUsername, toUsername, amount })
  },

  // 获取交易历史
  getTransactionHistory: async (walletId, page = 1, limit = 10) => {
    // 只缓存第一页数据
    const cacheKey = page === 1 ? `transactions_${walletId}` : null
    if (cacheKey) {
      const cached = getCachedData(cacheKey)
      if (cached) return cached
    }
    
    const data = await api.get(`/wallets/${walletId}/transactions/detailed`, {
      params: { page, limit }
    })
    
    if (cacheKey) {
      setCachedData(cacheKey, data)
    }
    return data
  }
}

export default api