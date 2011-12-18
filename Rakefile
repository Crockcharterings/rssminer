task :default => :test

version = Time.now.strftime("%Y%m%d%H%M") # timestamp
jscompiler = "closure-compiler.jar"
htmlcompressor = "htmlcompressor.jar"
luke = "lukeall-3.4.0_1.jar"

def get_file_as_string(filename)
  data = ''
  f = File.open(filename, "r")
  f.each_line do |line|
    data += line
  end
  return data
end

def get_dir(path)
  File.split(path)[0]
end

def gen_jstempls(folder)
  print "Generating #{folder}-tmpls.js, please wait....\n"
  html_tmpls = FileList["src/templates/tmpls/#{folder}/**/*.*"]
  data = "(function(){var tmpls = {};"
  html_tmpls.each do |f|
    text = get_file_as_string(f).gsub(/\s+/," ")
    name = File.basename(f,".tpl")
    data += "tmpls." + name + " = '" + text + "';\n"
  end
  data += "window.RM = {tmpls: tmpls};})();\n"
  File.open("public/js/gen/#{folder}-tmpls.js", 'w') {|f| f.write(data)}
end

def minify_js(name, jss, version)
  jscompiler = "closure-compiler.jar"
  target = "public/js/#{name}-#{version}-min.js"

  source_arg = ''
  jss.each do |js|
    source_arg += " --js #{js} "
  end
  # ADVANCED_OPTIMIZATIONS SIMPLE_OPTIMIZATIONS
  sh "java -jar bin/#{jscompiler} --warning_level QUIET " +
    "--compilation_level SIMPLE_OPTIMIZATIONS " +
    "--js_output_file '#{target}' #{source_arg}"
end

file "bin/#{jscompiler}" do
  mkdir_p 'bin'
  sh 'wget http://closure-compiler.googlecode.com/files/compiler-latest.zip' +
    ' -O /tmp/closure-compiler.zip'
  rm_rf '/tmp/compiler.jar'
  sh 'unzip /tmp/closure-compiler.zip compiler.jar -d /tmp'
  rm_rf '/tmp/closure-compiler.zip'
  mv '/tmp/compiler.jar', "bin/#{jscompiler}"
end

file "bin/#{htmlcompressor}" do
  mkdir_p 'bin'
  sh 'wget http://htmlcompressor.googlecode.com/files/htmlcompressor-1.3.1.jar' +
    " -O bin/#{htmlcompressor}"
end

file "bin/#{luke}" do
  mkdir_p 'bin'
  sh "wget http://luke.googlecode.com/files/#{luke} -O bin/#{luke}"
end

task :deps => ["bin/#{jscompiler}",
               "bin/#{luke}",
               "bin/#{htmlcompressor}"]

dashboard_jss = FileList['public/js/lib/jquery.js',
                         'public/js/lib/jquery.flot.js',
                         'public/js/lib/jquery.flot.pie.js',
                         'public/js/lib/underscore.js',
                         'public/js/lib/mustache.js',
                         'public/js/gen/dashboard-tmpls.js',
                         'public/js/rssminer/util.js',
                         'public/js/rssminer/plot.js',
                         'public/js/rssminer/dashboard.js']

mockup_jss = FileList['public/js/lib/zepto.js',
                      'public/js/lib/event.js',
                      'public/js/lib/underscore.js',
                      'public/js/lib/mustache.js',
                      'public/js/gen/mockup-tmpls.js',
                      'public/js/rssminer/mockup.js']

app_jss = FileList['public/js/lib/jquery.js',
                   'public/js/lib/underscore.js',
                   'public/js/lib/backbone.js',
                   'public/js/lib/mustache.js',
                   'public/js/gen/app-tmpls.js',
                   'public/js/rssminer/util.js',
                   'public/js/rssminer/views.js',
                   'public/js/rssminer/app.js']


desc "Clean generated files"
task :clean  do
  rm_rf 'public/js/rssminer/tmpls.js'
  rm_rf 'src/templates'
  rm_rf 'public/js/gen'
  rm_rf "public/css"
  rm_rf "classes"
  sh 'rm -vf public/js/*min.js'
end

desc "Prepare for development"
task :prepare => [:css_compile,:html_compress, "js:tmpls"]

desc "Prepare for production"
task :prepare_prod => [:css_compile, "js:minify"]

desc "Run server in dev profile"
task :run => :prepare do
  sh 'lein javac && scripts/run --profile dev'
end

desc "Run server in production profile"
task :run_prod => :prepare_prod do
  sh 'lein javac && scripts/run --profile prod'
end

desc 'Deploy to production'
task :deploy => [:clean, :test, :prepare_prod] do
  sh "scripts/deploy"
end

desc "Run unit test"
task :test => :prepare do
  sh 'lein javac && lein test'
end

namespace :js do
  desc "Generate template js resouces"
  task :tmpls => :html_compress do
    mkdir_p "public/js/gen"
    gen_jstempls("dashboard");
    gen_jstempls("app");
    gen_jstempls("mockup");
  end

  desc 'Combine all js into one, minify it using google closure'
  task :minify => [:tmpls, :deps] do
    minify_js("dashboard", dashboard_jss, version);
    minify_js("app", app_jss, version);
    minify_js("mockup", mockup_jss, version);
  end
end

scss = FileList['scss/**/*.scss'].exclude('scss/**/_*.scss')
desc 'Compile scss, compress generated css'
task :css_compile do
  mkdir_p "public/css"
  scss.each do |source|
    target = source.sub(/scss$/, 'css').sub(/^scss/, 'public/css')
    sh "sass -t compressed --cache-location /tmp #{source} #{target}"
    sh "sed -i \"s/{VERSION}/#{version}/g\" #{target}"
  end
end

html_srcs = FileList['templates/**/*.*']
html_triples = html_srcs.map {|f| [f, "src/#{f}", get_dir("src/#{f}")]}

html_triples.each do |src, tgt, dir|
  directory dir
  file tgt => [src, dir] do
    sh "java -jar bin/#{htmlcompressor} --charset utf8 #{src} -o #{tgt}"
    sh "sed -i \"s/{VERSION}/#{version}/g\" #{tgt}"
  end
end

desc 'Compress html using htmlcompressor, save compressed to src/templates'
task :html_compress => html_srcs.map {|f| "src/#{f}"}

namespace :watch do
  desc 'Watch css, html'
  task :all => [:deps, :css_compile, "js:tmpls"] do
    t1 = Thread.new do
      sh 'while inotifywait -r -e modify scss/; do rake css_compile; done'
    end
    t2 = Thread.new do
      sh 'while inotifywait -r -e modify templates/; do rake js:tmpls; done'
    end
    trap(:INT) {
      sh "killall inotifywait"
    }
    t1.join
    t2.join
  end
end
