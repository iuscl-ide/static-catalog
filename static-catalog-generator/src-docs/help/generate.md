## Generate the Catalog Files

The catalog generation is the last step in having a search-able catalog of static data. It needs to take the settings made in the previous step and apply it to the CSV data in order to produce the structure of files that can be downloaded and processed on the browser.

There are four necessary elements that must be provided to the generate-catalog engine

[![Generate Catalog]( ../screenshots/static-catalog--generate.png)]( ../screenshots/static-catalog--generate.png)

### The **source** CSV data file

This is the initial file, it can be viewed in the first tab and examined in the second tab

### The **fields-filters** file

This is the settings file which was created (edited) in the third tab during the "Fields and Filters" step

### The **template** file

A .liquid HTML template file to be personalized for the search (by default the dev one)

### The result folder

This folder will contain the generated catalog. Its content will be deleted on the start of each generation


The generation step will create several files and folders, inside a root folder (usually named "result"), on the following structure:

```_catalog```

This folder contains the data necessary for the search. Normally its content should never be modified after it is created. For information purposes only, here is its content:

```data```

Here is the source CSV data, split into pieces, each one less than one megabyte

```indexes```

Here are the values for values and interval indexes. There is one file per filter (field)

```keywords```

The keywords filters are kept here as a folder for each filter and a file for each two letters which are the begging of a word

```scripts```

This folder contains the scripts that do the actual search in the data. These scripts should not be modified

```sort```

The data sorts are kept here as one per field and direction (so maximum two per field – ascending and descending)


Again, this "_catalog" folder should be taken as it is, it will be re-generated (deleted, re-created and written) at each generate operation


Another generated folder is:

```_catalog-page```

This folder contains the search data which should be used in the html page, in order to display to the user the search options. It will contain:

```keywords```

This folder will contain the possible values (words) of the keyword filters
The structure is one folder per filter and inside it one file for each two letters pair which is the beginning of a keyword. Each file will contain all the words starting with a pair and for each word the number (counter) of lines (results) it appears.
These files can be used by specially written page controls to display to the user the possible words while the first letters are typed.
A complete example is given for the "Search Box" control (see below)

```static-catalog-page.json```

This file is the main setting file for the entire search page. It contains all the fields, and for each filed the settings from the file **fields-filters**, and if the field is a filter all possible filter values and for each value the number (counter) of lines (results) it appears on.

Is this file that have to be used to show the filters inside the page in order to allow the user to do a selection for search

This file offers all the necessary data in order to create the search controls, the way it will be used is up to the developer. It can be used in the page: to be used by a script that will dynamically create the controls, it could be used for already created controls to be filled with data, or it can be used as an entry for a templating system. By default Static-Catalog supports the Liquid template system, if a liquid file is provided the application will execute the directives inside on the data from static-catalog-page.json file.

A complete example for such a file is provided in the folder static-catalog-dev in the file static-catalog-page-dev.liquid
This file is part of a complete example with a style library (Semantic-UI) and own style/scripts. This default generation using the example template page can be used as a starting point in order to customize the result to match the containing site.

The idea is to use the default generation and then test the result, and when all will be OK, such as the controls, the result of the search, the performance etc. to start and customize the styles to match the desired look.

After generation, the html file which resulted from the liquid template will be opened in the default browser. This will allow to have a rapid view of the result, but this local file will not be functional, because of the browser domain constraints. For local files the current browsers do not allow for other resources to be opened by code. At the time of this writing Chrome is not allowing local JSON files to be loaded, by it allows for example custom local font files to load OK; for Firefox, it doesn’t allow anything.

> For Firefox there is a way to allow the local files, and everything will work fine, and this way can be tested at maximum speed, but it is not recommended for security reasons

As such, in order to test the generation a web server is needed (

