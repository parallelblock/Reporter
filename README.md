# Reporter

## Synopsis

ParallelBlock's Reporter plugin is a plugin designed to facilitate metrics
exporting between multiple plugins to an external Prometheus server. It also
supplies stub code for plugins wishing to implement metrics to handle cases
where the server both does and does not have the ParallelBlock Reporting plugin
present, defaulting to a no-op implementation of the metrics instrumentation
when the Reporting plugin is absent. This allows very easy and straightforward
instrumentation of plugins without complex logic while also giving the end
server owner a conditional choice and flexibility of configuration for whether
or not they desire to leverage the instrumentation provided by plugins.

## Why graphing?

Graphing at first may seem like it is more work than it is worth. After all,
advanced graphing of server metrics is a lot of work to set up, and the benefits
may not be immediately visible. This is the case when casually running a
Minecraft server. When casually managing a server, usually owners do not care
about how that server is actually doing - how many players, what the TPS is,
etc. As long as the server is online, further work on the server is overhead to
the end mission of the server (to have fun!) and something like a time-series
database is simply overkill. However, when venturing into the realm of serious
server hosting, metrics matter - with lots of players, it becomes much more
difficult to aggregate data.

Data, especially well aggregated data is powerful in deriving a variety of
insights from your server. Graphs are a great way of aggregating data and
getting insights - over time, you can monitor different metrics of your server
either by themselves or against each other. Graphing can be used to answer some
questions that previously are not easily answerable, like:

+ How many players are usually online during the afternoon on Thursdays?
+ How does my staff vs player ratio look, and is it consistent? Do I need to
  find more moderation staff during a different time of the day?
+ How does my server perform? Does the TPS drop when I hit a certain player
  count?
+ How reactive is my plugin? Are players feeling delays during database
  calls?
+ How does my global connection look? What does the distribution of latency to
  my players look over time? Is there a certain time of the day where the
  latency increases?

These are only examples of insights you can derive from graphing time-series
data. With more metrics integration into plugins, you can derive even more
knowledge from the graphing of metrics.

## Who is this for?

### Server Owners / Administrators

The ParallelBlock reporting plugin is an optional plugin - just because a plugin
is instrumented with the Reporting plugin does not require that the Reporting
plugin be actually present on the server. However, if you wish to export the
metrics which a plugin on your server is generating, the Reporting plugin must
be present on your server and configured to expose its data.

ParallelBlock's Reporter is designed with efficiency in mind - without the
plugin, the provided stub code for other plugins to use is effectively like the
instrumentation is not even there - when you choose not to use metrics, there
will be nearly no performance hit whatsoever compared to non-instrumented code.
When Reporter is installed, performance will only marginally be affected - great
care is taken to ensure that instrumentation does not cause any unexpected
performance issues.

### Plugin Developers

Instrumenting your code with the ParallelBlock Reporter instrumentation stub is
painfully simple, yet incredibly powerful in extracting value for servers. The
ParallelBlock stub is designed to be shaded into your plugin and is designed to
turn itself into a 'no-op' operation if the ParallelBlock Reporter plugin is not
enabled. The stub is small, efficient, simple, yet powerful for giving users of
your plugin more insight into the behavior of their server.
