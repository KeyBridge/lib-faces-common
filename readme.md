# lib-faces-common

A set of common JSF utilities and libraries to streamline JSF page
and backing bean development.

Includes compile dependencies for common JSF libraries such as
Primefaces, keybridge-CSS, etc.

History:

    v1.0.0 - collect common utilities into single JAR
    v2.0.0 - rewrite as maven release
    v2.0.1 - add getContextPath to FacesUtil
    v2.1.0 - PF and Omnifaces rev upgrade, refactor utilities into base package
    v2.1.1 - Omnifaces upgrade v2.2
    v2.1.2 - remove OmniFaces, add direct CDI accessor in AValidator
    v2.1.3 - update SSO soap client instantiators
    v2.1.4 - add forked OmniFaces ImportConstants tag handler
    v2.1.5 - add forked OmniFaces Ajax utility
    v2.2.0 - rewrite to minimize Primefaces dependency
    v2.3.0 - remove UserManager sign-in/out utility
    v2.4.0 - move SSO utility to lib-faces-sso, upgrade primefaces; move non-primefaces css to extra.css
    v2.4.1 - add abstract converter class; add evaluateExpression in FacesUtil
    v2.4.2 - remove primefaces extensions dep - these are now integrated into PF mainline
    v2.5.0 - remove all possible PF CSS, try to v4 bootstrapify
    v2.5.1 - add primefaces-none theme to disable all PF special formatting
    v2.6.0 -  upgrade using PF extensions 6.1.1; PF 6.1
    v2.6.1 - add Ajax shortcuts to FacesUtil
    v2.7.0 - add markdown; prettytime, brings prettytime converter
    v2.8.0 - add phone, local, zoned date time converters; rename existing converters
    v2.9.0 - consolidate string converters into one converter that accepts parameters

  
