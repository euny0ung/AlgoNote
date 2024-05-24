import { Html, Head, Main, NextScript } from 'next/document'

const Document = () => {
  return (
    <Html lang="en">
      <Head />
      <link
        rel="preload"
        as="image"
        href="https://algonote.s3.ap-northeast-2.amazonaws.com/your-image.jpg"
      />
      <body>
        <Main />
        <div id="modal-root" />
        <div id="modal2-root" />
        <NextScript />
      </body>
    </Html>
  )
}

export default Document
