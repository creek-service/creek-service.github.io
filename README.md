# Creek's GitHub pages site.

This GitHub repo has the source code for the root [Creek Service](https://www.creekservice.org) site.

The root is a folk of [mmistakes/minimal-mistakes](https://github.com/mmistakes/minimal-mistakes):
an awesome Jekyll theme, that's been modified to create a theme for the Creek site.

The `docs` folder contains the root [www.creekservice.org](https://www.creekservice.org) site.


## Setup

If you want to hack about with the site or add content, then follow these instructions to be able to run locally.

### Prerequisites

1. Install Git, obviously.
2. [Install Jekyll](https://jekyllrb.com/docs/installation)
3. Install [Builder](https://bundler.io/) by running `gem install bundler`.
4. Until [#752](https://github.com/github/pages-gem/issues/752) is resolved, run `bundle add webrick`.

### Installing

#### 1. Clone the repository

```shell
git clone git@github.com:creek-service/creek-service.github.io.git
cd creek-service.github.io
```
#### 2. Install the gems

```shell
bundle install
```
This will install Jekyll itself and any other gems that are needed.

#### 3. Update `github-pages` gem

The `github-pages` gem is regularly updated.
If out of date, the site may look different locally compared to when deployed on GitHub.
Update the gem and check in any changes.

```shell
git checkout main
git pull
bundle update github-pages
git checkout -b github-pages-update
git add .
git commit -m "updating github-pages gem"
git push --set-upstream origin github-pages-update
```

#### 4. Run the local server

```shell
bundle exec jekyll serve --livereload --baseurl /creek
```

This will launch a web server so that you can work on the site locally.
Check it out on [http://localhost:4000/creek](http://localhost:4000/creek).
