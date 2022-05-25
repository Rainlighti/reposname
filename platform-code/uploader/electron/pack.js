const {src, dest} = require('gulp');
const fs = require("fs")
const gulpIgnore = require('gulp-ignore');
const zip = require('gulp-zip');
const through = require('through2');
const gulpPlumber = require("gulp-plumber")
const path = require('path')

const pipeAllIgnorePattern =
    (input, patterns) =>
        patterns
            .reduce(
                (curr, acc) =>
                    curr.pipe(gulpIgnore.exclude(acc)
                    ), input);
const appendToList= function(list){

    return through.obj(function (file, enc, callback) {
        list.push(file)
        this.push(file);
        return callback();
    });
};
const done= function(cb){
    return through.obj(function (file, enc, callback) {
        cb(file)
        this.push(file);
        return callback();
    });
};
exports.packSource = (exclude,srcPath)=>new Promise(((resolve,reject) => {
    let packedFiles = [];
    pipeAllIgnorePattern(src([srcPath+"**/*"])
            .pipe(gulpPlumber({
                errorHandler:reject
            }))
        ,exclude)
        .pipe(appendToList(packedFiles))
        .pipe(zip("upload.zip"))
        .pipe(dest(srcPath))
        .pipe(done((product)=>resolve({
            packedFiles:packedFiles
                .map(it=>({
                    size:(it?._contents?.length)??0,
                    filename:it.basename,
                }))
            ,
            productSize:product._contents.length,
            file:fs.readFileSync(path.join(srcPath,'upload.zip'))
        })))


}))
// const path = require('path')
// var exclude = JSON.parse(fs.readFileSync("./uploader.config.json")).exclude
// exports.packSource(exclude,path.join(__dirname,"source/")).then()
