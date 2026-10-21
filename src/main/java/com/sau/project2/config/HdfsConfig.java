package com.sau.project2.config;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import java.net.URI;

@org.springframework.context.annotation.Configuration
public class HdfsConfig {

    @Bean(destroyMethod = "close")
    public FileSystem hdfs(@Value("${hdfs.uri}") String uri,
                           @Value("${hdfs.user}") String user) throws Exception {
        Configuration conf = new Configuration();   // Hadoop's Configuration
        conf.set("dfs.replication", "1");           // single node: 1 copy (client default is 3)
        return FileSystem.newInstance(URI.create(uri), conf, user);
    }
}