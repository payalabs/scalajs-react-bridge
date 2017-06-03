var config = module.exports = require("./webpack.config.js");
var webpack = require('webpack');
var _ = require('lodash');

config = _.merge(config, {
    externals : {
        "react" : "React",
        "react-dom" : "ReactDOM"
    }
});


var StringReplacePlugin = require("string-replace-webpack-plugin");

config.module.loaders.push({
    test: /index.html$/,
    loader: StringReplacePlugin.replace({
        replacements: [
            {
                pattern: /<!-- externals to be replaced by webpack StringReplacePlugin -->/ig,
                replacement: function (match, p1, offset, string) {
                    return '<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/react/0.14.0/react-with-addons.js"></script><script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/react/0.14.0/react-dom.js"></script>';
                }
            }
        ]})
    }
);

var definePlugin = new webpack.DefinePlugin({
  __PRODUCTION__ : true
});

config.plugins.push(
    new StringReplacePlugin(),
    definePlugin
)
