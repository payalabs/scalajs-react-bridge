"use strict";

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

/*
A generic component to facilitate testing.

This component allows passing any properties (scalar, array, map, and functions) and
creates children elements for each of them in such a way that tests can do simple
assertions to ensure that the bridging logic works as expected.

The created component nests one <span> child per property with the id set to the property
name and the text content set to the value encoded in the following manner:

- Scalar: string version of the value.
  Example: {name: "foo"} -> <span id="name">foo</span>
- Array: comma-separated list of encoded elements
  Example: {info: ["foo",5]} -> <span id="info">[foo,5]<span>
           {info: ["foo", [5,6]]} -> <span id="info">[foo,[5,6]]<span>
- Map: comma-separated list of encoded elements in the key->value format
  Example: {coordinate: {x: 5, y:6}} -> <span id="coordinate">{x->5,y->6}<span>

For functions, an `onClick` handler is installed, which calls the function set as the
property with array value set in TestComponent.eventTestData as individual parameters
to the function. For example, to test a (String,Int,String) => Unit function, you could set
TestComponent.eventTestData to, say, ["foo",5,"bar"]. Now when test simulates a click, the
TestComponent will invoke the function set as the property f("foo", 5, "bar").
*/

var TestComponent = function (_React$Component) {
    _inherits(TestComponent, _React$Component);

    function TestComponent() {
        _classCallCheck(this, TestComponent);

        return _possibleConstructorReturn(this, (TestComponent.__proto__ || Object.getPrototypeOf(TestComponent)).apply(this, arguments));
    }

    _createClass(TestComponent, [{
        key: "render",
        value: function render() {
            var _this2 = this;

            var props = this.props;

            var propKeys = Object.keys(props).filter(function (key) {
                return !(key == "children" || key == "key" || key == "ref");
            });

            var propElems = propKeys.map(function (key) {
                var value = props[key];
                if (typeof value === 'function') {
                    return React.createElement("span", {
                        id: key,
                        key: key,
                        onClick: _this2.testFunction(key, value) });
                } else {
                    return React.createElement("span", { id: key, key: key, "data-test": _this2.toTestString(value) });
                }
            });

            return React.createElement(
                "div",
                null,
                React.createElement(
                    "div",
                    { key: "props", id: "props" },
                    propElems
                ),
                React.createElement(
                    "div",
                    { key: "children", id: "children" },
                    props.children
                )
            );
        }
    }, {
        key: "toTestString",
        value: function toTestString(value) {
            var _this3 = this;

            if (Array.isArray(value)) {
                var encodedArray = value.map(function (element) {
                    return _this3.toTestString(element);
                }).join(',');
                return '[' + encodedArray + ']';
            } else if ((typeof value === "undefined" ? "undefined" : _typeof(value)) === "object") {
                var keys = Object.keys(value);
                var strings = keys.map(function (key) {
                    var v = value[key];
                    return key + "->" + _this3.toTestString(v);
                });
                return '{' + strings.join(',') + '}';
            } else {
                return value;
            }
        }
    }, {
        key: "testFunction",
        value: function testFunction(key, f) {
            return function (e) {
                f.apply(this, TestComponent.eventTestData);
            };
        }
    }]);

    return TestComponent;
}(React.Component);

TestComponent.eventTestData = [];