import type { ReactNode } from 'react'
import './styles.css'

export const metadata = {
  description: 'پنل محتوای آموزشی و صدای AVA',
  title: 'AVA CMS Prototype',
}

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html dir="rtl" lang="fa">
      <body>{children}</body>
    </html>
  )
}
