__webpack_public_path__ = "/";

React = require("react");
ReactDOM = require("react-dom");

if (__PRODUCTION__) {
    require('./target/scala-2.12/scalajs-react-bridge-example-opt.js');
} else {
    require('./target/scala-2.12/scalajs-react-bridge-example-fastopt.js');
}

require('file?name=./index.html!./index.html');

TagsInput = require('react-tagsinput');
require('react-tagsinput/react-tagsinput.css');

ReactMediumEditor = require('react-medium-editor').default;
require('medium-editor/dist/css/medium-editor.css');
require('medium-editor/dist/css/themes/flat.css');

ReactBootstrap = require("react-bootstrap");
