



let path= "/Users/thevoid/dev/os-exp/score-sys/uploader/electron/out/uploader-darwin-x64/uploader.app/Contents/Resources/app"
const re = /.*\/(.*?)(?:\/.*?){3}?$/
const result = re.exec(path)
if(result!==null)
    console.log(result[1])
