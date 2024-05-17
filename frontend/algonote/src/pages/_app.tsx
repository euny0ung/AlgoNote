import { useEffect, useState } from 'react'
import { useRouter } from 'next/router'
import type { AppProps } from 'next/app'
import '@/styles/globals.scss'
import Footer from '@/components/commons/Footer'
import NavBar from '@/components/commons/NavBar'
import NoteNavBar from '@/components/commons/NoteNavBar'
import SSE from '@/components/commons/Notification/SSE/SSE'
import { getCookie } from '@/utils/cookie'

const App = ({ Component, pageProps }: AppProps) => {
  const [isLoggedIn, setIsLoggedIn] = useState<boolean | null>(null)
  const router = useRouter()
  const url = router.pathname
  useEffect(() => {
    const accessToken = getCookie('access_token')
    if (accessToken) {
      setIsLoggedIn(true)
    } else {
      setIsLoggedIn(false)
    }
  }, [url])

  return (
    <>
      {router.pathname === '/writenote' || router.pathname === '/revisenote' ? (
        <NoteNavBar />
      ) : (
        <NavBar />
      )}
      <Component {...pageProps} />
      {isLoggedIn ? <SSE /> : null}
      {router.pathname !== '/writenote' && router.pathname !== '/revisenote' ? (
        <Footer />
      ) : null}
    </>
  )
}

export default App
