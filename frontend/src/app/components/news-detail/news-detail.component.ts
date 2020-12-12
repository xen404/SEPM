import { Component, OnInit } from '@angular/core';
import * as _ from 'lodash';
import {ActivatedRoute} from '@angular/router';
import {NewsService} from '../../services/news.service';
import {News} from '../../dtos/news';
import {formatDate} from '@angular/common';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';


@Component({
  selector: 'app-news-detail',
  templateUrl: './news-detail.component.html',
  styleUrls: ['./news-detail.component.css']
})
export class NewsDetailComponent implements OnInit {
  error: boolean = false;
  errorMsg: string;
  news: News;
  image: string;


  constructor(private route: ActivatedRoute, private newsService: NewsService, private sanitizer: DomSanitizer) { }

  ngOnInit(): void {
    this.loadNewsDetails(Number(this.route.snapshot.paramMap.get('id')));
  }

 /**
   * Loads the text of news and update the existing array of news
   * @param id the id of the news which details should be loaded
   */
  loadNewsDetails(id: number) {
    this.newsService.getNewsById(id).subscribe(
      (news: News) => {
        this.news = news;
        this.getImage(this.news.id);
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMsg = error.error.error;
    } else {
      this.errorMsg = error.error;
    }
  }


  getImage(id:number)
     {

    this.newsService.getImages(id).subscribe(
      (res) => {
       this.image=res
          }
        );

     }

     getSanitizer(id:number)
     {
                     return this.sanitizer.bypassSecurityTrustResourceUrl(this.image);

  }


}
