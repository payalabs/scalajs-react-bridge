# scalajs-react bridge

A simple way to make React components in the wild usable in [scalajs-react](https://github.com/japgolly/scalajs-react) apps. Write an object for each component, use one of the two provided macros that takes care of any boilerplate code, and start using it in a type-safe manner in scalajs-react apps.

For example, to create a component corresponding to [react-tagsinput](https://github.com/olahol/react-tagsinput), define a class as follows:

```scala
object TagsInput extends ReactBridgeComponent {
  def apply(id: js.UndefOr[String]  = js.undefined, className: js.UndefOr[String] = js.undefined,
            ref: js.UndefOr[String] = js.undefined, key: js.UndefOr[Key] = js.undefined,
            defaultValue: js.UndefOr[Seq[String]] = js.undefined,
            value: js.UndefOr[Seq[String]] = js.undefined,
            placeholder: js.UndefOr[String] = js.undefined,
            onChange: js.UndefOr[js.Array[String] => Callback] = js.undefined,
            validate: js.UndefOr[String => CallbackTo[Boolean]] = js.undefined,
            transform: js.UndefOr[String => CallbackTo[String]] = js.undefined): ComponentNoChild = 
    this.autoConstructNoChild
}
```

The core logic of bridging the JS React component to scala-react is implemented using two macros that you can use
as an implementation of the `apply` method.
1. `autoConstruct`: Use this method for the components that may have children
2. `autoConstructNoChild`: Use this for the component that cannot have children 

Then use it in a scalajs-react app the same way as any other component.

```scala
div(
  TagsInput(value = Seq("foo","bar"), onChange = printSequence _)
)
```

## Getting started

1. Add the Sonatype snapshots resolver to your SBT configuration:
 ```scala
resolvers += Resolver.sonatypeRepo("snapshots")
```

2. Add the following dependency to your scalajs-react project:
 ```scala
libraryDependencies += "com.payalabs" %%% "scalajs-react-bridge" % "0.4.0-SNAPSHOT"
```

## Defining components

This core class `ReactBridgeComponent` assumes that the objects extending it follows these conventions:
- The object name must correspond to the function name exposed for the underlying component.
- The object must have exactly one `apply` method. Each parameter name of the `apply` method must correspond to the property name of the underlying component.
- Each `apply` method parameter type must map to the underlying component's expected property
  type. For example, if the underlying component expects a string property, then the parameter type must be `String`. The bridge automatically translates (through implicit converters) parameters with `Seq` type (or its subtypes) to js array and `Map` types with `String` key type to js literal. You may provide custom conversions for your own types by introducing an implicit value of  the [JsWriter](https://github.com/payalabs/scalajs-react-bridge/blob/master/src/main/scala/com/payalabs/scalajs/react/bridge/ReactBridgeComponent.scala) type.

## Example components

### [See them live](https://payalabs.github.io/scalajs-react-bridge-example)

- [ReactMediumEditor](https://github.com/payalabs/scalajs-react-bridge-example/blob/master/src/main/scala/com/payalabs/scalajs/react/bridge/elements/ReactMediumEditor.scala)
- [TagsInput](https://github.com/payalabs/scalajs-react-bridge-example/blob/master/src/main/scala/com/payalabs/scalajs/react/bridge/elements/TagsInput.scala)
- [Bootstrap Button](https://github.com/payalabs/scalajs-react-bridge-example/blob/master/src/main/scala/com/payalabs/scalajs/react/bridge/elements/Button.scala)
- [Bootstrap Input](https://github.com/payalabs/scalajs-react-bridge-example/blob/master/src/main/scala/com/payalabs/scalajs/react/bridge/elements/Input.scala)
