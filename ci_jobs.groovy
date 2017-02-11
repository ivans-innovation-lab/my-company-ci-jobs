
// define the repos we want to build on CI
def repos = [ 'my-company-common','my-company-monolithic-web' ]

// create a multibranch pipeline job for each of the repos
for (repo in repos)
{
  multibranchPipelineJob("${repo}") {

    // build master as well as feature branches 
    branchSources {
      git {
        remote("https://github.com/ivans-innovation-lab/${repo}.git")
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
  // automatically queue the job after the initial creation
  if (!jenkins.model.Jenkins.instance.getItemByFullName("${repo}")) {
    queue("${repo}")
  }
}

