import { useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { useWallet } from '../context/WalletContext'
import { languageStorage } from '../utils/languageStorage'
import '../styles/LanguageSelector.css'

// 语言选项 - 简化为简洁的显示名称
const languages = [
  { code: 'en-US', shortName: 'EN' },
  { code: 'ja-JP', shortName: '日' },
  { code: 'zh-CN', shortName: '简' },
  { code: 'zh-TW', shortName: '繁' }
]

function LanguageSelector() {
  const { i18n } = useTranslation()
  const { currentLanguage, dispatch } = useWallet()

  // 同步i18n语言与钱包上下文
  useEffect(() => {
    if (i18n.language !== currentLanguage) {
      i18n.changeLanguage(currentLanguage)
    }
  }, [i18n, currentLanguage])

  // 处理语言切换
  const handleLanguageChange = (languageCode) => {
    if (languageStorage.isSupported(languageCode) && languageCode !== currentLanguage) {
      i18n.changeLanguage(languageCode)
      dispatch({ type: 'SET_LANGUAGE', payload: languageCode })
      languageStorage.setLanguage(languageCode)
    }
  }

  return (
    <div className="language-selector-simple">
      {languages.map((lang) => (
        <button
          key={lang.code}
          className={`language-button ${currentLanguage === lang.code ? 'language-button--active' : ''}`}
          onClick={() => handleLanguageChange(lang.code)}
          type="button"
          aria-label={`Switch to ${lang.code}`}
          title={`Switch to ${lang.code}`}
        >
          {lang.shortName}
        </button>
      ))}
    </div>
  )
}

export default LanguageSelector