var webpack = require('webpack');
var path = require('path');

module.exports = {
    context: __dirname,
    entry: './main.js',
    output: {
        path: path.join(__dirname, 'dist'),
        filename: 'bundle.js'
    },
    module: {
        loaders: [
            {
              test: /\.scss$/,
              loader: 'style!css!sass?outputStyle=expanded'
            },
            {
              test: /\.less$/,
              loader: 'style!css!less'
            },
            {
              test: /\.css$/,
              loader: 'style!css?outputStyle=expanded'
            },
            { test: /\.woff$/, loader: "url-loader?limit=10000&mimetype=application/font-woff" },
            { test: /\.ttf$/,  loader: "url-loader?limit=10000&mimetype=application/octet-stream" },
            { test: /\.eot$/,  loader: "file-loader" },
            { test: /\.svg$/,  loader: "url-loader?limit=10000&mimetype=image/svg+xml" },
            { test: /\.png$/, loader: "url-loader?mimetype=image/png" }
        ]
    },
    plugins: [
    ]
};
