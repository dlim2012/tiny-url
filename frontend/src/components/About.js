
import ReactMarkdown from 'react-markdown'
import { Divider } from 'antd'


export const About = () => {
    return <>
    <ReactMarkdown>
      # Tiny-URL System 
      </ReactMarkdown>
      <hr  />

      <ReactMarkdown>## Technologies</ReactMarkdown>
      <Divider />
      <ReactMarkdown>* Spring Boot 3.0.2</ReactMarkdown>
      <ReactMarkdown>* Spring Cloud 2022.0.1</ReactMarkdown>
      <ReactMarkdown>* Spring Security</ReactMarkdown>
      <ReactMarkdown>* JSON Web Tokens (JWT) </ReactMarkdown>
      <ReactMarkdown>* OAuth 2.0 </ReactMarkdown>
      <ReactMarkdown>* React 18.2.0 </ReactMarkdown>
      <ReactMarkdown>* Maven </ReactMarkdown>

      <ReactMarkdown>## Design</ReactMarkdown>
      <Divider />
      <ReactMarkdown>* For system design specifications, please refer to the [design documentation](https://github.com/dlim2012/tiny-url-system-design/blob/main/Design%20doc.pdf).</ReactMarkdown>
      
  </>
}
