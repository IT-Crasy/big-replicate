# Big Replicate

Command-line tool for [Google Cloud BigQuery](https://cloud.google.com/bigquery/).

Provides two core features:

1. **Sync**: will copy/synchronise Google Analytics session tables between different datasets/projects. For example, moving Google Analytics data from a US dataset into the EU.
2. **Materialize**: executes a statement and outputs to a table. Useful for materializing views/complex intermediate tables.

[![CircleCI](https://circleci.com/gh/uswitch/big-replicate.svg?style=svg)](https://circleci.com/gh/uswitch/big-replicate)

## Usage

### Synchronising Data

The destination project must have a dataset (with the same name as the source) that already exists. It will look for any session tables that are missing from the destination dataset and replicate the `--number` of most recent ones. We use the `--number` parameter to help incrementally copy very large datasets over time.

```bash
export GCLOUD_PROJECT="source-project-id"
export GOOGLE_APPLICATION_CREDENTIALS="./service-account-key.json"

export JVM_OPTS="-Dlogback.configurationFile=./logback.example.xml"

java $JVM_OPTS -cp big-replicate-standalone.jar \
  uswitch.big_replicate.sync \
  --source-project source-project-id \
  --source-dataset 98909919 \
  --destination-project destination-project-id \
  --destination-dataset 98909919 \
  --table-filter "ga_sessions_\d+" \
  --google-cloud-bucket gs://staging-data-bucket \
  --number 30
```

Because only missing tables from the destination dataset are processed tables will not be overwritten. 

The example above is intended for replicating [Google Analytics BigQuery data](https://support.google.com/analytics/answer/3437618?hl=en) from one project to another. It works by:

* Specifying `--table-filter` to only replicate tables matching the expected `ga_sessions_\d+` filter. This can be any valid Java regular expression. 
* Specifying `--number` restricts the replication to 30 tables. Tables are reverse ordered lexicographically by the tool.

This ensures that the most recent 30 days of tables that don't exist (in the `--destination-project` and `--destination-dataset`) but do in the sources will be replicated.

`big-replicate` will run multiple extract and loads concurrently- this is currently set to the number of available processors (as reported by the JVM runtime). You can override this with the `--number-of-agents` flag. Since no processing is performed client-side (all operations are BigQuery jobs) its safe to set this well above the processor count.

### Materializing Data

We often use views to help break apart more complex queries, building join tables between datasets etc. The `materialize` operation executes a statement and stores the output in a table. 

```bash
export GCLOUD_PROJECT="source-project-id"
export GOOGLE_APPLICATION_CREDENTIALS="./service-account-key.json"

export JVM_OPTS="-Dlogback.configurationFile=./logback.example.xml"

echo "SELECT * FROM [dataset.sample_table]" | java $JVM_OPTS \
  -cp big-replicate-standalone.jar \
  uswitch.big_replicate.materialize \
  --project-id destination-project-id \
  --dataset-id destination-dataset-id \
  --table-id destination-table \
  --force
```

## Releases

Binaries are built on [CircleCI](https://circleci.com/gh/uswitch/big-replicate) with artifacts pushed to [GitHub Releases](https://github.com/uswitch/big-replicate/releases). The published jar is suitable for running directly as above.

## Building

The tool is written in [Clojure](https://clojure.org) and requires [Leiningen](https://github.com/technomancy/leiningen).

```
$ make
```

## License

Copyright © 2016 uSwitch

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
