var config = module.exports = require("./webpack.config.js");
var webpack = require('webpack');
var _ = require('lodash');

config = _.merge(config, {
	devtool: "eval-source-map",
	debug: true,
	pathinfo: true,
	historyApiFallback: true
});

var definePlugin = new webpack.DefinePlugin({
  __PRODUCTION__ : false
});

config.plugins.push(
    definePlugin
)