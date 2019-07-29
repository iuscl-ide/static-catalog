### Conclusions

The most important points to consider when creating a static catalog are:

#### Careful creation of the CSV file

* The file should contain only the necessary data. For example, if a field has only one value there is no point for it to be included, its value can be included as a constant in the results. As stated in the initial requirements, all the lines should be distinct, so is better to re-verify that directly in the CSV if possible
* The data should be the same type on a field column with one or just a few exceptions
* Each value should be a label and not an ID, as the CSV should be seen as the only available table, there are no other sheets or tables to do a join. If not possible (the CSV is provided and not possible to be changed – for example because it is a database dump and is too big to be edited) the values can be replaced at the generation phase, but this is easy to be done only for a few values, not to replace an entire table
* Even if the catalog can have almost ten million lines, the performance degrades for large data. The simplest way to have a much better performance is to split the data by the values of a field (a form of data sharding) by partitioning it in smaller CSVs each one representing a value of the field. This way a search page can be creating for each CSV; however this should be done only if the data for the field and its values makes sense to be split (for example for a super category field with values such as "Electronics" and "Clothes" it makes sense to be partitioned because the user will search for one or the other and not both at once)

#### No more sorting than necessary

* By their nature the sort files could be very large, for input CSV data with millions of lines, a sort file size could approach 100 megabytes. GitHub will not accept files over 100 MB (it will give a warning for files over 50 MB but will accept them)
* If no sorting is used the result lines will be returned in the order the lines are in the CSV file. This is also the fastest way to display the results as no sorting is done. So, put the CSV data in the sort order the most often used by the user
* For some data fields it doesn’t makes sense to have both ascending and descending sorting (for example it makes sense to have the "Given Name" sorted ascending but not so to have it sorted descending, and the same could be sid for "Popularity" or "Number of Reviews")
* Also, a big number of sort options could be disconcerting for the user

#### The filter values should make sense

* Too many values and checkboxes will confuse the user. Try creating intervals by grouping the values reasonably
* There is not a very big performance difference between one or more checked values for a field but for a lot of data it counts; so, if it's possible (the data makes sense) to have radio buttons or a dropdown and choose only one value, it could help

#### Always use the default template to test

* The default template is the reference point for the correct functionality, use it and get back to it until it works as desired, and then use a personalized template

#### Do a full re-generation, do not try to manually replace pieces

* Even if a single value (such a price) changes and the temptation is to just modify it inside the generated block data files, do not do it, modify the input data CSV and re-generate everything. After all, *it is static data*



