import ReactMarkdown from 'react-markdown'
import { Divider } from 'antd'

export const Home = () => {
    return (
        <>

      <ReactMarkdown># Tiny-URL System</ReactMarkdown>
      <hr  />
        <a>A personal project made to practice system design. </a> <a href="https://github.com/dlim2012/tiny-url-system">(GitHub)</a>
        
      <br />
      <br />
      <ReactMarkdown>## Features</ReactMarkdown>
      <Divider />
      <ReactMarkdown>* Short URL generation and redirection service</ReactMarkdown>
      <ReactMarkdown>* User registration and login</ReactMarkdown>
      <ReactMarkdown>* User URL allowance and management</ReactMarkdown>
      <ReactMarkdown>* Custom URL path</ReactMarkdown>
      <ReactMarkdown>* Private URL service</ReactMarkdown>
      </>
    )
}