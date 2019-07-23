### What is static data?

Static data represents any data which will no longer going to be modified

Some examples are:

The data is the result of a past event (an exam, a census, an experiment)
The values of the data are for a specific period (the store prices of articles for the current day, the books entered in a library before the current month)
The data values are constants (scientific values and tables, internal standard procedures)

### What are the advantages of **static-catalog**?

The same advantages as for the static generated sites. Just HTML, CSS, JSON, CSV and JS, all read only.
No database, PHP, CMS with incompatible versions and requirements
No more issues with moving the data from the development database to the production database
The data is still of course generated from a database or other data source, but its management and transformation are done inhouse and there is no need to be aligned with the server stack requirements

### What are the use cases?

A lot of the data we encounter is static, meaning that it will not change anymore. Some examples are:

### What about performance optimization such as data compression and local caching?

Modern browsers already use compression when downloading files, a file is compress with *gzip* on the server and decompress on the browser. Also, a file once downloaded is not downloaded again for the same session (unless the page is refreshed)

These optimizations can be verified by activating the network tab of the browser. So, the best thing in our case for the way **static-catalog** works is to let the browser do the file compression and the caching, as is faster than can be done in JS
