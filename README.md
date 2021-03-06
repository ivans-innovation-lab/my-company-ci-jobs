
# Jenkins Jobs

Repository containing the job definitions for the [Jenkins server](https://github.com/ivans-innovation-lab/my-company-infrastructure):
 * it uses [Job-DSL](https://github.com/jenkinsci/job-dsl-plugin/wiki) to describe **WHAT** (i.e. which repos) to build
 * the **HOW** to build is defined in each of the referenced repo's `Jenkinsfile`s

## How it works

There is a [seed-job](https://github.com/ivans-innovation-lab/my-company-infrastructure/blob/master/seedJob.xml) configured in ['my-company-infrastructure' repository](https://github.com/ivans-innovation-lab/my-company-infrastructure) which runs every minute and applies the job-dsl scripts in all the `*.groovy` files within this repository.

The [`ci_jobs.groovy`](https://github.com/ivans-innovation-lab/my-company-ci-jobs/blob/master/ci_jobs.groovy) sets up the CI build jobs/pipelines for our projects.
```groovy
// define the repos we want to build on CI
def repos = [ 'my-company-common','my-company-project-materialized-view','my-company-project-domain','my-company-blog-materialized-view','my-company-blog-domain','my-company-monolith' ]
```

It then loops over all the repos and creates a [multibranchPipelineJob](https://jenkinsci.github.io/job-dsl-plugin/#method/javaposse.jobdsl.dsl.DslFactory.multibranchPipelineJob) (building both `master` and `feature/*` branches) for each of them:

```groovy
// create a multibranch pipeline job for each of the repos
for (repo in repos)
{
  multibranchPipelineJob("${repo}") {

    // build master as well as feature branches
    branchSources {
      git {
        remote("https://github.com/ivans-innovation-lab/${repo}.git")
        credentialsId('git')
        includes("master feature/*")
      }
    }
    // check every minute for scm changes as well as new / deleted branches
    triggers {
      periodic(1)
    }
    // don't keep build jobs for deleted branches
    orphanedItemStrategy {
      discardOldItems {
        numToKeep(0)
      }
    }
  }
}

```

Once a build is started, it will look for a `Jenkinsfile` and run the build pipeline that is defined therein (see [my-company-monolithic-web](https://github.com/ivans-innovation-lab/my-company-monolith)).


## Where to go from here?

Now that you have a minimal "running pipeline jobs via Job-DSL" example running, here are some ideas on how to explore the topic further:

 * use the [API-Viewer](https://jenkinsci.github.io/job-dsl-plugin/) to explore the available Job-DSL items
 * use the [Job-DSL Playground](https://job-dsl.herokuapp.com/) to play around with and test your job-dsl snippets
 * define which branches you want to build
 * organize your jobs into views
 * use the [Github Branch Source Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+Branch+Source+Plugin) /  [Bitbucket Branch Source Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Bitbucket+Branch+Source+Plugin) to automatically build all projects within your organization / project
 * ...
