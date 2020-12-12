import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {NewsService} from '../../services/news.service';
import {News} from '../../dtos/news';
import {NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import * as _ from 'lodash';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';



@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  private news: News[];
  newsForm: FormGroup;
  images: string[] = [];
  retrievedImage: string;


  constructor(private newsService: NewsService, private ngbPaginationConfig: NgbPaginationConfig, private formBuilder: FormBuilder,
                            private cd: ChangeDetectorRef, public authService: AuthService, private route: ActivatedRoute,  private router: Router, private sanitizer: DomSanitizer) {

                             this.newsForm = this.formBuilder.group({
                                  title: ['', [Validators.required]],
                                  summary: ['', [Validators.required]],
                                  text: ['', [Validators.required]],
                                });}

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.loadNews();
    }
   }

     getNews(): News[] {
       return this.news;
     }

    /**
      * Loads the text of news and update the existing array of news
      * @param id the id of the news which details should be loaded
      */
     loadNewsDetails(id: number) {
       this.newsService.setSeenNewsById(id).subscribe(
         (news: News) => {
           const result = this.news.find(x => x.id === id);
           result.text = news.text;

         });

         this.router.navigate(['news-detail',id]);


     }

      /**
        * Loads the specified page of news from the backend
        */
       private loadNews() {
         this.newsService.getLastUnseenNews().subscribe(
           (news: News[]) => {
             this.news = news;

             for(var n of this.news)
                      {
                       //if((this.news.find(x => x.id === n.id).imagePresent)==false)
                         this.getImage(n.id);
                     }

           }
         );
       }

      /* exit() {
            window.location.reload();
         }*/


         /**
          * Shows the specified news details. If it is necessary, the details text will be loaded
          * @param id the id of the news which details should be shown
          */
         getNewsDetails(id: number) {
           if (_.isEmpty(this.news.find(x => x.id === id).text)) {
             this.loadNewsDetails(id);

           }
         }


      getImage(id:number)
         {

        this.newsService.getImages(id).subscribe(
          (res) => {
           this.images[id]=res
           this.news.find(x => x.id === id).imagePresent=true;
              }
            );

         }

      getSanitizer(id:number)
         {
                         return this.sanitizer.bypassSecurityTrustResourceUrl(this.images[id]);

      }

}
