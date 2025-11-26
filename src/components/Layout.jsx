import { useTranslation } from 'react-i18next'
import LanguageSelector from './LanguageSelector'
import ExchangeRateBanner from './ExchangeRateBanner'

function Layout({ children }) {
  const { t } = useTranslation()

  return (
    <div className="app-layout">
      <header className="app-header">
        <h1 className="app-title">𝙲𝚑𝚛𝚢𝚜𝚘𝚛𝚛𝚑𝚘𝚎 GO</h1>
      </header>
      <ExchangeRateBanner />
      <main id="main-content" className="app-main">
        {children}
      </main>
      <footer className="app-footer">
        <div className="app-footer__content">
          <LanguageSelector />
        </div>
      </footer>
    </div>
  )
}

export default Layout