
export function RedirectService(props) {
    console.log("Redirecting... " + props.state);
    window.open('http://localhost:8080/' + props.state, "_self")
  }
