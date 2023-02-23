
import ReactMarkdown from 'react-markdown'


export const About = () => {
  const codestring = `const Square = (n) => return n * n`; 
    return <>
    <ReactMarkdown>
      # Tiny-URL System 
      </ReactMarkdown>
      {/* <ReactMarkdown>[project](Project Link: https://github.com/dlim2012/tiny-url-system)</ReactMarkdown> */}
      

      <ReactMarkdown>## Technologies</ReactMarkdown>
      <ReactMarkdown>* Spring Boot 3.0.2</ReactMarkdown>
      <ReactMarkdown>* Spring Cloud 2022.0.1</ReactMarkdown>
      <ReactMarkdown>* Spring Security</ReactMarkdown>
      <ReactMarkdown>* JSON Web Tokens (JWT) </ReactMarkdown>
      <ReactMarkdown>* OAuth 2.0 </ReactMarkdown>
      <ReactMarkdown>* React 18.2.0 </ReactMarkdown>
      <ReactMarkdown>* Maven </ReactMarkdown>

      <ReactMarkdown>## Design</ReactMarkdown>
      <ReactMarkdown>* For system design specifications, please refer to the [design documentation](https://github.com/dlim2012/tiny-url-system-design/blob/main/Design%20doc.pdf).</ReactMarkdown>
      
  </>
}