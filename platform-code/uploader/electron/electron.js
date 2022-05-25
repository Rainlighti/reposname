const {app, BrowserWindow} = require('electron')
const {ipcMain} = require('electron')
const fs = require("fs");
const path = require('path')
const {packSource} = require("./pack")

function getAppRoot() {
    if (process.platform === 'win32') {
        return path.join(app.getAppPath(), '/../../../');
    }
    else if (process.platform === 'linux') {
        return path.join(app.getAppPath(), '/../../');
    }
    else {
        return path.join(app.getAppPath(), '/../../../../');
    }
}

function getAppName() {
    const path = app.getAppPath();
    let re = null;
    if (process.platform === 'win32') {
        re = /.*\/(.*?)(?:\/.*?){2}?$/;
    } else {
        re = /.*\/(.*?)(?:\/.*?){3}?$/;
    }
    const result = re.exec(path)
    return result !== null ? result[1]
        : 'uploader'
}

console.log(getAppName())
let cwd = getAppRoot()
console.log(cwd);
const configPath = path.join(cwd, "uploader.config.json");
let buf = fs.readFileSync(configPath)
let config = JSON.parse(buf.toString());
const srcPath = path.join(cwd);

function getConfigExclude() {
    if (config?.exclude) {
        let exclude = [...config?.exclude];
        exclude.push(getAppName() + "/**/*")
        exclude.push(getAppName())
        return exclude;
    }
    return config?.exclude;
}

ipcMain.on("get-config",
    (e) => {
        let temp = {...config};
        temp.exclude = getConfigExclude();
        e.returnValue = temp;
    })
ipcMain.on("pack", (e) => {
    console.log("recv pack");
    packSource(getConfigExclude(), srcPath)
        .catch((error) => e.sender.send("pack-error", error))
        .then((info) => e.sender.send("pack-done", info))
})
ipcMain.on("pre-pack", (e) => {
    packSource(getConfigExclude(), srcPath)
        .catch((error) => e.sender.send("pre-pack-error", error))
        .then((info) => e.sender.send("pre-pack-done", info))
})
ipcMain.on("set-token", ((event, args) => {
    config.token = args
    fs.writeFileSync(configPath, JSON.stringify(config), {flag: 'w+'})
    event.returnValue = 1;
}))
ipcMain.on("get-token", ((event, args) => {
    let buf = fs.readFileSync(configPath)
    config = JSON.parse(buf.toString());
    event.returnValue = config.token;
}))

// packSource(config.exclude, srcPath).then()

function createWindow() {
    const win = new BrowserWindow({
        // width: 480,
        width: config.debug ? 1000 : 480,
        // height: 545,
        height: 555,
        webPreferences: {
            nodeIntegration: true,
            preload: path.join('preload.js'),
        },
    })

    if (config.debug)
        win.webContents.toggleDevTools()
    win.loadFile('./dist/index.html')
    ipcMain.on('reload', () => {
        win.reload();
        win.webContents.reload()
    })
}

app.whenReady().then(createWindow)

app.on('window-all-closed', () => {
    app.quit()
})

app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
        createWindow()
    }
})

function startUpload(event) {


}

ipcMain.handle("start-upload", startUpload)









