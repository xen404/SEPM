package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedNewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchandiseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleNewsDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface NewsMapper {

    @Named("simpleNews")
    SimpleNewsDto newsToSimpleNewsDto(News message);


    @IterableMapping(qualifiedByName = "simpleNews")
    List<SimpleNewsDto> newsToSimpleNewsDto(List<News> message);

    DetailedNewsDto newsToDetailedNewsDto(News message);

    News detailedNewsDtoToNews(DetailedNewsDto detailedMessageDto);

    News newsInquiryDtoToNews(NewsInquiryDto messageInquiryDto);

    NewsInquiryDto newsToNewsInquiryDto(News message);


}

