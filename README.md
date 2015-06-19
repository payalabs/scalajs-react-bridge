# scalajs-react bridge

A simple way to make React components in the wild usable in [scalajs-react](https://github.com/japgolly/scalajs-react) apps. Write a case class for each component and start using it in a type-safe manner in scalajs-react apps.

For example, to create a component corresponding to [react-tagsinput](https://github.com/olahol/react-tagsinput), define a class as follows:

```scala
case class TagsInput(id: js.UndefOr[String]  = js.undefined,
                     className: js.UndefOr[String] = js.undefined,
                     ref: js.UndefOr[String] = js.undefined,
                     key: js.UndefOr[Any] = js.undefined,
                     defaultValue: js.UndefOr[Seq[String]] = js.undefined,
                     value: js.UndefOr[Array[String]] = js.undefined,
                     placeholder: js.UndefOr[String] = js.undefined,
                     onChange: js.UndefOr[js.Array[String] => Unit] = js.undefined,
                     validate: js.UndefOr[String => Boolean] = js.undefined,
                     transform: js.UndefOr[String => String] = js.undefined)
    extends ReactBridgeComponent
```

Then use it in a scalajs-react app the same way as any other component.

```scala
div(
  TagsInput(defaultValue = Seq("foo","bar"), onChange = printSequence _)
)
```

## Getting started

1. Clone this repository
2. Publish to local sbt repository
 ```
$ sbt publish
```

3. Add the following dependency to your scalajs-react project
 ```scala
libraryDependencies += "com.payalabs" %%% "scalajs-react-bridge" % "0.1.0"
```

## Defining components

This core class `ReactBridgeComponent` assumes that the classes extending it follows these conventions:
- The class name must correspond to the function name exposed for the underlying component.
- Each case class parameter name must correspond to the property name of the underlying component.
- Each case class parameter type must map to the underlying component's expected property
  type. For example, if the underlying component expects a string property, then the parameter type must be `String`. The bridge automatically translates parameters with `Seq` type (or its subtypes) to js array and `Map` types to js literal (currently makes an unchecked assumption that the key is of the String type).

## Example components

### [See them live](https://payalabs.github.io/scalajs-react-bridge-example)

- [ReactMediumEditor](https://github.com/payalabs/scalajs-react-bridge-example/blob/master/src/main/scala/com/payalabs/scalajs/react/bridge/elements/ReactMediumEditor.scala)
- [TagsInput](https://github.com/payalabs/scalajs-react-bridge-example/blob/master/src/main/scala/com/payalabs/scalajs/react/bridge/elements/TagsInput.scala)
- [Bootstrap Button](https://github.com/payalabs/scalajs-react-bridge-example/blob/master/src/main/scala/com/payalabs/scalajs/react/bridge/elements/Button.scala)
- [Bootstrap Input](https://github.com/payalabs/scalajs-react-bridge-example/blob/master/src/main/scala/com/payalabs/scalajs/react/bridge/elements/Input.scala)
