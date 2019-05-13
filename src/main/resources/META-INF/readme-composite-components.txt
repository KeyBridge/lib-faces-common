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




Defining the Custom Component Tag in a Tag Library Descriptor

To use a custom tag, you declare it in a Tag Library Descriptor (TLD). 
The TLD file defines how the custom tag is used in a JavaServer Faces page. 
The web container uses the TLD to validate the tag. The set of tags that 
are part of the HTML render kit are defined in the HTML_BASIC TLD, available 
at http://docs.oracle.com/javaee/6/javaserverfaces/2.1/docs/renderkitdocs/.

The TLD file name must end with taglib.xml. In the Duke's Bookstore case study,
 the custom tags area and map are defined in the file web/WEB-INF/bookstore.taglib.xml.

All tag definitions must be nested inside the facelet-taglib element in the 
TLD. Each tag is defined by a tag element that specifies a particular 
combination of a component type and a renderer type. 


The facelet-taglib element must also include a namespace element.

The TLD file is located in the WEB-INF directory. In addition, an entry is 
included in the web deployment descriptor (web.xml) to identify the custom tag library descriptor file, as follows:

    <context-param>
        <param-name>javax.faces.FACELETS_LIBRARIES</param-name>
        <param-value>/WEB-INF/bookstore.taglib.xml</param-value>
    </context-param>

TLD files are stored in the WEB-INF/ directory or subdirectory of the WAR file 
or in the META-INF/ directory or subdirectory of a tag library packaged in a JAR file.

