package com.sl.redcare.gitclient;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GitSearchMapper {

    GitSearchResult responseToDto(GitSearchItem response);
}
