

import { ModifyPath } from "./ModifyPath"


export const ModifyPathWrapper = () => {
    localStorage.setItem("longUrl", "")
    localStorage.setItem("isPrivate", "X")
    return <ModifyPath />
}
