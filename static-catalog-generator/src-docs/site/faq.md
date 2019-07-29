### What is static data?

Static data represents any data which will no longer going to be modified

Some examples are:

* The data is the result of a past event (an exam, a census, an experiment)
* The values of the data are for a specific period (the card activity for the last month, the store prices of articles for the current day, the books entered in a library before the current month)
* The data values are constants (scientific values and tables, internal standard procedures)

Any data can be used in **static-catalog** generation as long as it is clear that only this data will be available for search and if the data changes then the catalog has to be re-generated

### What are the advantages of **static-catalog**?

The same advantages as for the static generated sites

* Just HTML, CSS, JSON, CSV and JS, all read only
* No database, PHP, CMS with incompatible versions and requirements
* No more issues with moving the data from the development database to the production database

The data is still of course generated from a database or other data source, but its management and transformation are done locally and there is no need to be aligned with the server stack requirements

### What are the use cases?

The first use case is for small businesses which maintain an Excel file with the price offer and the file is available to be downloaded from their site. So, every morning or the day before, a person from the enterprise takes the prices from the internal system, creates the Excel file (in fact refill the template), uploads it to the server and the clients download it and do a search to find what product are in the offer and the current prices. The Excel file only helps them with the fact that is standard to open, it has some colors and formatted text in order to help identify the products and can be searched by keyword. For this use case, instead of creating an Excel file a CSV file can be created and a **static-catalog** generated with a template; the resulted catalog files can be uploaded to the server and the user will have a much better experience being able to search in the page by multiple criteria.

Another use case is for a teacher that after an exam or after grading some papers to have the results list exported as an CSV, and then with **static-catalog** to create a catalog, and then publish it on the internal intranet in his own pages (blog, wiki) that the institution provided. This way the students will just enter into the page and search for their name in order to find their grades. Again, this is very easy to do as is just a matter of uploading (copy) some files, there is no database

And finally, any data could be treated as static if it is clear that it contains a certain period, this way any scenario can be implemented, for example: for the phone numbers of the residents of a city, if the catalog is changed every week it can be consider as static data for that week as long as clearly mentioned that any modification will be available the following week.<br>
Another example, a card data activity for the last month and the latest three months can be generated on the start of a new month for the user can search it; and only for the current month it can be a different (simpler) page which will search in the database, this way the system can be faster, the current activity to be searched differently from the past activity

### What about performance optimization such as data compression and local caching?

Modern browsers already use compression when downloading files, a file is compress with *gzip* on the server and decompress on the browser. Also, a file once downloaded will not be downloaded again for the same session (unless the page is refreshed)

These optimizations can be verified by activating the network tab of the browser. So, the best thing in this case for the way **static-catalog** works is to let the browser do the file compression and the caching, as it is faster than being done in JS
