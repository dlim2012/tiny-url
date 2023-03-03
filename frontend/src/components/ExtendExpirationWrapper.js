

import { ExtendExpiration } from "./ExtendExpiration"


export const ExtendExpirationWrapper = () => {
    localStorage.setItem("longUrl", "");
    localStorage.setItem("isPrivate", "X");
    return <ExtendExpiration />
}
