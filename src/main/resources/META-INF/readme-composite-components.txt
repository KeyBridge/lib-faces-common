# Setting the component ID

Composite components implicitly inherit from NamingContainer and
already prepend their own id to those of children.

To reference the composite component from outside by ajax as in
<f:ajax render="compositeId" />, then wrap the body of <cc:implementation>
in a plain vanilla HTML <span> or <div> with the #{cc.clientId}. e.g.

```xml
<cc:implementation>

  <div id="#{cc.clientId}">

    content ...

  </div>

</cc:implementation>
```


