package com.gi.demo.dtos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.io.File;

@Entity
@Table(name = "clients")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Client {
    @jakarta.persistence.Id
    @Column (name = "serverName")
    public String name;
    public String host;
    public String path;
    public String serverType;
    public int port;
    public int min_memory;
    public int max_memory;

    public File getFile(){
        return new File(this.path);
    }
}
