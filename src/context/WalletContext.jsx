import { createContext, useContext, useReducer, useEffect, useMemo } from 'react'
import WalletService from '../services/walletService'
import { languageStorage } from '../utils/languageStorage'

const WalletContext = createContext()

const initialState = {
  currentWallet: null,
  transactions: [],
  currentLanguage: languageStorage.getLanguage(),
  isLoading: false,
  error: null,
  pagination: {
    currentPage: 1,
    totalPages: 1,
    totalTransactions: 0,
    hasNextPage: false,
    hasPreviousPage: false,
    limit: 10
  }
}

function walletReducer(state, action) {
  switch (action.type) {
    case 'SET_LOADING':
      return { ...state, isLoading: action.payload }
    case 'SET_ERROR':
      return { ...state, error: action.payload, isLoading: false }
    case 'SET_WALLET':
      return { ...state, currentWallet: action.payload, error: null }
    case 'SET_TRANSACTIONS':
      return { ...state, transactions: action.payload }
    case 'SET_LANGUAGE':
      return { ...state, currentLanguage: action.payload }
    case 'SET_PAGINATION':
      return { ...state, pagination: action.payload }
    case 'CLEAR_ERROR':
      return { ...state, error: null }
    default:
      return state
  }
}

export function WalletProvider({ children }) {
  const [state, dispatch] = useReducer(walletReducer, initialState)

  // 合并初始化逻辑，减少渲染次数
  useEffect(() => {
    // 合并初始化逻辑，减少渲染次数
    const initializeApp = () => {
      // 加载钱包数据
      const savedWallet = localStorage.getItem('wallet')
      if (savedWallet) {
        try {
          const wallet = JSON.parse(savedWallet)
          dispatch({ type: 'SET_WALLET', payload: wallet })
        } catch (error) {
          localStorage.removeItem('wallet')
        }
      } else {
        // 添加模拟钱包数据，确保在开发环境或API不可用时能看到内容
        const mockWallet = {
          id: 'mock-wallet-id',
          username: 'DemoUser',
          balance: 1000,
          createdAt: new Date().toISOString()
        }
        dispatch({ type: 'SET_WALLET', payload: mockWallet })
        
        // 添加模拟交易历史数据
        const mockTransactions = [
          {
            id: 'trans-1',
            fromWalletId: 'mock-wallet-id',
            toWalletId: 'wallet-2',
            fromUsername: 'DemoUser',
            toUsername: 'Alice',
            amount: 200,
            type: 'TRANSFER',
            status: 'COMPLETED',
            timestamp: new Date(Date.now() - 1000 * 60 * 30).toISOString()
          },
          {
            id: 'trans-2',
            fromWalletId: 'wallet-3',
            toWalletId: 'mock-wallet-id',
            fromUsername: 'Bob',
            toUsername: 'DemoUser',
            amount: 500,
            type: 'TRANSFER',
            status: 'COMPLETED',
            timestamp: new Date(Date.now() - 1000 * 60 * 60 * 2).toISOString()
          },
          {
            id: 'trans-3',
            fromWalletId: null,
            toWalletId: 'mock-wallet-id',
            fromUsername: null,
            toUsername: 'DemoUser',
            amount: 1000,
            type: 'CDK_REDEEM',
            status: 'COMPLETED',
            timestamp: new Date(Date.now() - 1000 * 60 * 60 * 24).toISOString()
          }
        ]
        dispatch({ type: 'SET_TRANSACTIONS', payload: mockTransactions })
        
        // 设置模拟分页数据
        const mockPagination = {
          currentPage: 1,
          totalPages: 1,
          totalTransactions: 3,
          limit: 10,
          hasNextPage: false,
          hasPreviousPage: false
        }
        dispatch({ type: 'SET_PAGINATION', payload: mockPagination })
      }
      
      // 设置语言
      const savedLanguage = languageStorage.getLanguage()
      const language = savedLanguage && languageStorage.isSupported(savedLanguage) 
        ? savedLanguage 
        : languageStorage.getBrowserLanguage()
        
      dispatch({ type: 'SET_LANGUAGE', payload: language })
      
      if (!savedLanguage) {
        languageStorage.setLanguage(language)
      }
    }
    
    initializeApp()
  }, [])

  // 仅创建一次walletService实例
  const walletService = useMemo(() => new WalletService(dispatch), [])

  // 记忆化context值，避免不必要的重新渲染
  const value = useMemo(() => ({
    ...state,
    walletService,
    dispatch // 添加dispatch以支持组件中的直接状态更新
  }), [state, walletService])

  return (
    <WalletContext.Provider value={value}>
      {children}
    </WalletContext.Provider>
  )
}

export function useWallet() {
  const context = useContext(WalletContext)
  if (!context) {
    throw new Error('useWallet must be used within a WalletProvider')
  }
  return context
}

export default WalletProvider