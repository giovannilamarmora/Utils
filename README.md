# Utils

This Repository group all of my Utils needs into every code to be imported.

This page will be refreshed every time an utils will be added.

## How to use it
Copy this dependency into your maven project.

```
<dependency>
  <groupId>com.github.giovannilamarmora.utils</groupId>
  <artifactId>utils</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

Then you need to set your settings.xml file to be able to read the dependency from GitHub.

```
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <activeProfiles>
        <activeProfile>github</activeProfile>
    </activeProfiles>
    <profiles>
        <profile>
            <id>github</id>
            <repositories>
                <repository>
                    <id>central</id>
                    <url>https://repo1.maven.org/maven2</url>
                </repository>
                <repository>
                    <id>github</id>
                    <url>https://maven.pkg.github.com/giovannilamarmora/utils</url>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                </repository>
            </repositories>
        </profile>
    </profiles>

    <servers>
        <server>
            <id>github</id>
            <username>YOUR_GITHUB_USERNAME</username>
            <password>YOUR_GITHUB_ACCESS_TOKEN</password>
        </server>
    </servers>

</settings>
```

The Access Token could be founded into Account settings, under Developer settings, Personal Access Token.
Create a new one with read package permission.

Then Run the `mvn install` to install the dependency.