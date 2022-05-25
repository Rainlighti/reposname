const {ipcRenderer} = window.require('electron')
export function getConfig()
{
    return ipcRenderer
        .sendSync("get-config")
}
