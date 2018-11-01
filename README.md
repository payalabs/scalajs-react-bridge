# scalajs-react bridge

A boilerplate-free way to make React components in the wild usable in [scalajs-react](https://github.com/japgolly/scalajs-react) apps. Write an object for each component, use one of the four provided macros, and start using it in a type-safe manner in scalajs-react apps.

For example, to create a component corresponding to [react-tagsinput](https://github.com/olahol/react-tagsinput), define a class as follows:

```scala
object TagsInput extends ReactBridgeComponent {
  def apply(defaultValue: js.UndefOr[Seq[String]] = js.undefined,
            value: js.UndefOr[Seq[String]] = js.undefined,
            placeholder: js.UndefOr[String] = js.undefined,
            onChange: js.UndefOr[js.Array[String] => Callback] = js.undefined,
            validate: js.UndefOr[String => CallbackTo[Boolean]] = js.undefined,
            transform: js.UndefOr[String => CallbackTo[String]] = js.undefined): WithPropsNoChildren = autoNoChildren
}
```
Then use it in a scalajs-react app the same way as any other component.

```scala
div(
  TagsInput(value = Seq("foo","bar"), onChange = printSequence _)
)
```

If you want to pass DOM attributes as well as React special attributes such as "key" as additional properties, you can easily do so as follows:

```scala
div(
  TagsInput(value = Seq("foo","bar"), onChange = printSequence _)(className := "tags", key := "key-1")
)
```
Finally, while `TagsInput` doesn't allow children (as signified by the return type of the method), if it were to, you could pass children as follows:

```scala
div(
  TagsInput(value = Seq("foo","bar"), onChange = printSequence _)(className := "tags", key := "key-1")(
    "child1",
    div(className := "some-div")(
      span(className: "some-span")("content")
    )
  )
)
```

## Getting started

Add the following dependency to your scalajs-react project:
 ```scala
libraryDependencies += "com.payalabs" %%% "scalajs-react-bridge" % "0.7.0"
```

To use the latest snapshot version

1. Add the Sonatype snapshots resolver to your SBT configuration:
 ```scala
resolvers += Resolver.sonatypeRepo("snapshots")
```

2. Add the following dependency to your scalajs-react project:
 ```scala
libraryDependencies += "com.payalabs" %%% "scalajs-react-bridge" % "0.7.0-SNAPSHOT"
```

## Defining components

The core logic of bridging the JS React component to scala-react is implemented using the `ReactBridgeComponent` class and four macros in it that you can use as an implementation of an `apply` method (stricly speaking, you could use any name for the method, but then the component usage won't look as natural). The macro you will use depends on if the component allows children and if the component accepts arbitrary DOM attributes (`TagsMod`s).

|                           | Can have children | Cannot have children      |
|---------------------------|-------------------|---------------------------|
| **Can take DOM attrs**    | `auto`            | `autoNoChildren`          |
| **Cannot take DOM attrs** | `autoNoTagMods`   | `autoNoTagModsNoChildren` |

Each of the macros return type that signify what has been already processed (and thus cannot process it again).
- `auto`: `WithProps` (properties have been consumed, thus can pass `TagMod`s followed by children)
- `autoNoChildren`: `WithPropsNoChildren` (properties have been consumed, thus can pass `TagMod`s, but that cannot be followed by children)
- `autoNoTagMods`: `WithPropsAndTags` (properties have been consumed as are `TagMod`s, thus can be followed by children)
- `autoNoTagModsNoChildren`: `WithPropsAndTagsNoChildren` (properties, tags, and children have been consumed)

### The easy path

`ReactBridgeComponent` offers an easy way to bridge a component when an object extending it follows these conventions:
1. The object name matches the function name exposed for the underlying component. For example, if the component object is declared as `object MyComponent extends ReactBridgeComponent { ... }`, the correspoding `MyComponent` is available in the global space. 
2. The object has any number of `apply` methods taking properties as arguments. Each apply method may be implemented as either `autoConstruct` or `autoConstructNoChildren`. The default property transformation assumes that each method parameter type maps to the underlying component's expected property type and the parameter name match the underlying components property name. For example, if the underlying component expects a string property with name `foo`, then the parameter type must be `String` and parameter name must be `foo`. The bridge automatically translates (through implicit converters) parameters with `Seq` type (or its subtypes) to js array and `Map` types with `String` key type to js literal. You may provide custom conversions for your own types by introducing an implicit value of  the [JsWriter](https://github.com/payalabs/scalajs-react-bridge/blob/master/src/main/scala/com/payalabs/scalajs/react/bridge/JsWriter.scala) type.

### Overriding the default
If a component cannot follow the expected conventions, it can override them as following:
- If the class name doesn't match the function name, it can override `componentName` supply a different name. 
- If the function isn't exposed in the global space, it can override `componentNamespace` to supply the path to the function. For example, if the component function is exposed as `foo.bar.MyComponent`, you can override `componentNamespace` to return `foo.bar`.
- You may override `componentValue` to use any `js.Any` you can reference. This works well with `@JSImport`ed objects.
- If overriding `componentName` and/or `componentNamespace` isn't sufficient, you may override `jsComponent` to supply the component function.
- If an apply method's parameters require transformation beyond what is implemented by the macros, don't use the macros as their implementation. Instead, supply your own implementation, which may still use the `jsComponent` after transforming the method parameters appropriately.

### Special cases

#### Passing DOM attributes to component
Oftentimes, React components allow adding any DOM attributes in addition to properties specific to that component. By default, bridged components allow passing any DOM attributes (as `TagMod`s). Behind the scene, these attributes are merged with specific propeties passed. If you don't want this behavior, you can use the appropriate varation of macro described in the table earlier.

#### Component without any special properties
Oftentimes, especially with components that simply enhance a regular DOM element such as &lt;input&gt; don't need any special properties beyond what can be passed as DOM attributes. To handle those cases, `scalajs-react-bridge` offers `ReactBridgeComponentNoSpecialProps` (which extends `ReactBridgeComponent`). You can extend this class without implementing anything else thus making it a one-liner.

```scala
object Button extends ReactBridgeComponentNoSpecialProps
```

which then may be passed any DOM attributes (as TagMod):
```scala
Button(onClick --> handleClick)("Simple Button")
```

#### Component without any special properties and without children
As a further special case, certain components may not take any children, either. Those components may extend `ReactBridgeComponentNoSpecialPropsNoChildren` without implemented anything else thus making it a one-liner.

```scala
object Input extends ReactBridgeComponentNoPropsNoChildren
```

which then may be used as
```scala
Input(value := currentValue, onChange ==> handleChange)
```

## Example components

### [See them live](https://payalabs.github.io/scalajs-react-bridge-example)

- [ReactMediumEditor](https://github.com/payalabs/scalajs-react-bridge/blob/master/example/src/main/scala/com/payalabs/scalajs/react/bridge/elements/ReactMediumEditor.scala)
- [TagsInput](https://github.com/payalabs/scalajs-react-bridge/blob/master/example/src/main/scala/com/payalabs/scalajs/react/bridge/elements/TagsInput.scala)
- [Bootstrap Button](https://github.com/payalabs/scalajs-react-bridge/blob/master/example/src/main/scala/com/payalabs/scalajs/react/bridge/elements/Button.scala)
- [Bootstrap Input](https://github.com/payalabs/scalajs-react-bridge/blob/master/example/src/main/scala/com/payalabs/scalajs/react/bridge/elements/Input.scala)
