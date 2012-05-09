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
  data = "(function(){var c = Mustache.compile; var tmpls = {};"
  html_tmpls.each do |f|
    text = get_file_as_string(f).gsub(/\s+/," ")
    name = File.basename(f, ".tpl")
    data += "tmpls." + name + " = c('" + text + "');\n"
  end
  data += "window.RM = {tmpls: tmpls};})();\n"
  File.open("public/js/gen/#{folder}-tmpls.js", 'w') {|f| f.write(data)}
end

def minify_js(target, jss)
  jscompiler = "closure-compiler.jar"
  source_arg = ''
  jss.each do |js|
    source_arg += " --js #{js} "
  end
  # ADVANCED_OPTIMIZATIONS SIMPLE_OPTIMIZATIONS
  sh "java -jar thirdparty/#{jscompiler} --warning_level QUIET " +
    "--compilation_level SIMPLE_OPTIMIZATIONS " +
    "--js_output_file '#{target}' #{source_arg}"
end

file "thirdparty/#{jscompiler}" do
  mkdir_p "thirdparty"
  sh 'wget http://closure-compiler.googlecode.com/files/compiler-latest.zip' +
    ' -O /tmp/closure-compiler.zip'
  rm_rf '/tmp/compiler.jar'
  sh 'unzip /tmp/closure-compiler.zip compiler.jar -d /tmp'
  rm_rf '/tmp/closure-compiler.zip'
  mv '/tmp/compiler.jar', "thirdparty/#{jscompiler}"
end

file "thirdparty/#{htmlcompressor}" do
  mkdir_p "thirdparty"
  sh 'wget http://htmlcompressor.googlecode.com/files/htmlcompressor-1.3.1.jar' +
    " -O thirdparty/#{htmlcompressor}"
end

file "thirdparty/#{luke}" do
  mkdir_p "thirdparty"
  sh "wget http://luke.googlecode.com/files/#{luke} " +
    "-O thirdparty/#{luke}"
end

task :deps => ["thirdparty/#{jscompiler}",
               "thirdparty/#{luke}",
               "thirdparty/#{htmlcompressor}"]

dashboard_jss = FileList['public/js/lib/jquery.flot.js',
                         'public/js/lib/jquery.flot.pie.js',
                         'public/js/lib/underscore.js',
                         'public/js/lib/mustache.js',
                         'public/js/gen/dashboard-tmpls.js',
                         'public/js/rssminer/util.js',
                         'public/js/rssminer/plot.js',
                         'public/js/rssminer/dashboard.js']

landing_jss = FileList['public/js/lib/slides.min.jquery.js',
                       'public/js/rssminer/landing.js']

app_jss = FileList['public/js/lib/jquery-ui-1.8.18.custom.js',
                   'public/js/lib/underscore.js',
                   'public/js/lib/mustache.js',
                   'public/js/gen/app-tmpls.js',
                   'public/js/rssminer/util.js',
                   'public/js/rssminer/ajax.js',
                   'public/js/rssminer/router.js',
                   'public/js/rssminer/layout.js',
                   'public/js/rssminer/rm_data.js',
                   'public/js/rssminer/search.js',
                   'public/js/rssminer/app.js']

desc "Clean generated files"
task :clean  do
  rm_rf 'public/js/rssminer/tmpls.js'
  rm_rf 'src/templates'
  rm_rf 'public/rssminer.crx'
  rm_rf 'public/js/gen'
  rm_rf "public/css"
  rm_rf "classes"
  sh 'rm -vf public/js/*min.js'
end

desc "Prepare for development"
task :prepare => [:css_compile,:html_compress, "js:tmpls"]

desc "Prepare for production"
task :prepare_prod => [:css_compile, "js:minify"]

desc "lein swank"
task :swank do
  sh "rm classes -rf && lein javac && lein swank"
end

desc 'Deploy to production'
task :deploy => [:clean, :chrome, :test, :prepare_prod] do
  sh "scripts/deploy"
end

desc "Run unit test"
task :test => :prepare do
  sh 'rm classes -rf && lein javac && lein test'
end

desc "Generate TAGS using etags for clj"
task :etags do
  rm_rf 'TAGS'
  sh %q"find . \! -name '.*' -name '*.clj' | xargs etags --regex='/[ \t\(]*def[a-z]* \([a-z-!0-9?]+\)/\1/'"
end

namespace :js do
  desc "Generate template js resouces"
  task :tmpls => :html_compress do
    mkdir_p "public/js/gen"
    gen_jstempls("dashboard");
    gen_jstempls("app");
    gen_jstempls("chrome");
    sh 'mv public/js/gen/chrome-tmpls.js chrome/tmpls.js'
  end

  desc 'Combine all js into one, minify it using google closure'
  task :minify => [:tmpls, :deps] do
    minify_js("public/js/dashboard-min.js", dashboard_jss);
    minify_js("public/js/app-min.js", app_jss);
    minify_js("public/js/landing-min.js", landing_jss);
  end
end

scss = FileList['scss/**/*.scss'].exclude('scss/**/_*.scss')
desc 'Compile scss, compress generated css'
task :css_compile do
  mkdir_p "public/css"
  scss.each do |source|
    target = source.sub(/scss$/, 'css').sub(/^scss/, 'public/css')
    sh "sass -t compressed --cache-location /tmp #{source} #{target}"
  end
  sh "find public/css/ -type f " +
    "| xargs -I {} sed -i \"s/{VERSION}/#{version}/g\" {}"
  sh 'mv public/css/chrome.css chrome/style.css'
end

desc "create chrome extension"
task :chrome do
  sh 'google-chrome --pack-extension=chrome ' +
    '--pack-extension-key=conf/chrome.pem'
  sh 'mv chrome.crx public/rssminer.crx'
end

desc "update dev mysql config file, restart mysql"
task :mysql_dev do
  sh 'sudo /etc/init.d/mysql stop'
  sh 'sudo rm /tmp/mysql -rf && sudo rm /tmp/mysql.log -f'
  sh 'sudo cp /var/lib/mysql /tmp -r '
  sh 'sudo chown mysql:mysql /tmp/mysql -R'
  sh 'sudo cp conf/my-dev.cnf /etc/mysql/my.cnf'
  sh 'sudo /etc/init.d/mysql start'
  sh 'rake db:restore_db'
  # sh 'scripts/admin -d rssminer -u feng init-db'
end

desc "update dev mysql config file, restart mysql"
task :mysql_prod do
  sh 'sudo /etc/init.d/mysql stop'
  sh 'sudo cp conf/my.cnf /etc/mysql/my.cnf'
  sh 'sudo /etc/init.d/mysql start'
  sh 'rake db:restore_db'
end

desc 'Compress html using htmlcompressor, save compressed to src/templates'
task :html_compress do
  html_srcs = FileList['templates/**/*.*']
  html_triples = html_srcs.map {|f| [f, "src/#{f}", get_dir("src/#{f}")]}
  html_triples.each do |src, tgt, dir|
    if !File.exist?(dir)
      mkdir_p dir
    end
    if !File.exist?(tgt) || File.mtime(tgt) < File.mtime(src)
      sh "java -jar thirdparty/#{htmlcompressor} --charset utf8 #{src} -o #{tgt}"
    end
  end
  sh "find src/templates/ -type f " +
    "| xargs -I {} sed -i \"s/{VERSION}/#{version}/g\" {}"
end

desc "Using luke to inspect luence index"
task :luke do
  sh 'java -jar thirdparty/lukeall-3.4.0_1.jar -index /var/rssminer/index &'
end

namespace :db do
  desc "Reload database with production data"
  task :backup_prod do
    sh 'rm classes -rf && lein javac'
    sh './scripts/admin backup-db && ./scripts/admin restore-db && ./scripts/admin rebuild-index'
  end

  desc "Restore db from latest backup"
  task :restore_db do
    sh 'rm classes -rf && lein javac'
    sh './scripts/admin restore-db && ./scripts/admin rebuild-index'
  end
end

namespace :run do
  desc "Run server in dev profile"
  task :dev => :prepare do
    sh 'rm classes -rf && lein javac && scripts/run --profile dev'
  end

  desc "Run server in production profile"
  task :prod => :prepare_prod do
    sh 'rm classes -rf && lein javac && scripts/run --profile prod'
  end

  desc "Restore db from latest backup, Run server in dev profile"
  task :restore_db_dev => ["db:restore_db", "run:dev"]
end

namespace :watch do
  desc 'Watch css, html'
  task :all => [:deps, :css_compile, "js:tmpls"] do
    # sh "echo -n \"\033]0;rssminer rake watch\007\""
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
