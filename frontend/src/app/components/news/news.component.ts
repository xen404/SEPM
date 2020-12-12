import {ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {NewsService} from '../../services/news.service';
import {News} from '../../dtos/news';
import * as _ from 'lodash';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {Router, ActivatedRoute} from '@angular/router';
import {NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'app-news',
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.scss']
})
export class NewsComponent implements OnInit {

  @ViewChild('closeModal') closeModal: ElementRef;
  error: boolean = false;
  errorMessage: string = '';
  newsForm: FormGroup;
  // After first submission attempt, form validation will start
  submitted: boolean = false;
  private news: News[];
  selectedFile: File = null;
  imageError: string;
  isImageSaved: boolean;
  cardImageBase64: string;
  allowedTypes = ['image/png', 'image/jpeg'];
  public imagePath;
  imgURL: any;
  private id: number;
  images: string[] = [];
  newsUnseen: News[];

  successCreated: boolean = false;
  successCreatedMessage: string = '';

  constructor(private newsService: NewsService,
              private ngbPaginationConfig: NgbPaginationConfig,
              private formBuilder: FormBuilder,
              private cd: ChangeDetectorRef,
              private authService: AuthService,
              private sanitizer: DomSanitizer,
              private route: ActivatedRoute,
              private router: Router) {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    this.newsForm = this.formBuilder.group({
      title: ['', [Validators.required]],
      summary: ['', [Validators.required]],
      text: ['', [Validators.required]],
      image: ['', Validators.required]
    });
  }
  pageSeen: number = 0;
  pagesSeen: number[];
  paginationLimitUpSeen: number = 15;
  paginationLimitDownSeen: number = 0;

  pageUnseen: number = 0;
  pagesUnseen: number[];
  paginationLimitUpUnseen: number = 15;
  paginationLimitDownUnseen: number = 0;
  pageSize: number = 8;

  newsNum: number;
  seen: boolean = false;
  unseen: boolean = true;




  ngOnInit() {
    this.loadSeenNews();
    this.loadUnseenNews();

    const successString = this.route.snapshot.paramMap.get('createdNews');
    if (successString != null) {
      this.successCreated = true;
      this.successCreatedMessage = 'You have successfully published the news article: ' + successString;
    }
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  /**
   * Starts form validation and builds a news dto for sending a creation request if the form is valid.
   * If the procedure was successful, the form will be cleared.
   */
  addNews() {
    this.submitted = true;
    if (this.newsForm.valid) {
      const news: News = new News(null,
        this.newsForm.controls.title.value,
        this.newsForm.controls.summary.value,
        this.newsForm.controls.text.value,
        new Date().toISOString(),
        null,
        false
      );
      this.createNews(news);
      this.clearForm();
    } else {
      console.log('Invalid input');
    }
  }

  /**
   * Save image to database
   */
  renderImage() {
    this.newsService.getLastImage().subscribe(
      (ns) => {
        if (this.selectedFile != null) {
          const fd = new FormData();
          fd.append('file', this.selectedFile, this.selectedFile.name);
          this.newsService.uploadImage(ns, fd).subscribe(res => {
            console.log(res);
          });
        }
      },
      error => {
        this.defaultServiceErrorHandling(error);
      });
  }

  /**
   * Sends news creation request
   * @param news the news which should be created
   */
  createNews(news: News) {
    this.newsService.createNews(news).subscribe(
      (msg: News) => {
        this.id = msg.id;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      },
      () => {
        this.renderImage();
        this.loadUnseenNews();
        this.closeModal.nativeElement.click();
        this.router.navigate(['/news', {createdNews: news.title}]);
      }
    );
  }

  getSeenNews(): News[] {
    return this.news;
  }

   getUnseenNews(): News[] {
      return this.newsUnseen;
    }

  /**
   * Shows the specified news details. If it is necessary, the details text will be loaded
   * @param id the id of the news which details should be shown
   */
  getNewsDetails(id: number) {
    if (_.isEmpty(this.newsUnseen.find(x => x.id === id).text)) {
      this.loadNewsDetails(id);
    }
  }

  /**
   * Save image with specific id to image array
   * @param id the id of the news
   */
  getImage(id: number) {
    this.newsService.getImages(id).subscribe(
      (res) => {
        this.images[id] = res;
        this.news.find(x => x.id === id).imagePresent = true;
        console.log('Get image for news item with id:' + id);
      }
    );
  }

  /**
   * Use sanitizer to secure image
   * @param id the id of the news
   */
  getSanitizer(id: number) {
    return this.sanitizer.bypassSecurityTrustResourceUrl(this.images[id]);
  }

  /**
   * Loads the text of news and update the existing array of news
   * @param id the id of the news which details should be loaded
   */
  loadNewsDetails(id: number) {

    this.newsService.setSeenNewsById(id).subscribe(
      (newsUnseen: News) => {
        const result = this.newsUnseen.find(x => x.id === id);
        result.text = newsUnseen.text;

      });

    this.router.navigate(['news-detail', id]);

  }


  /**
   * load image in FileReader
   */
  onFileSelected(event) {
    this.imageError = null;
    if (event.target.files && event.target.files[0]) {
      this.selectedFile = <File>event.target.files[0];


      if (!_.includes(this.allowedTypes, this.selectedFile.type)) {
        console.log('Only images are allowed (JPEG/PNG)');
        this.imageError = 'Only images are allowed (JPEG/PNG)';
        return false;
      }

      const reader = new FileReader();
      this.imagePath = event.target.files;
      reader.onload = (e: any) => {
        const img = new Image();
        img.src = e.target.result;
        img.onload = rs => {

          this.cardImageBase64 = e.target.result;
          this.isImageSaved = true;
          this.newsForm.controls.image.setValue(this.cardImageBase64);
          this.imgURL = reader.result;
        };
      };
      reader.readAsDataURL(this.selectedFile);
    }
  }


  /**
   * Error flag will be deactivated, which clears the error news
   */
  vanishError() {
    this.error = false;
  }

  /**
   * Loads the specified page of news from the backend
   */
  private loadUnseenNews() {
    this.newsService.getUnseenNews(this.pageUnseen).subscribe(
      data => {
        this.newsUnseen = data['content'];
        this.newsNum = data['totalElements'];
        this.pagesUnseen = new Array(data['totalPages']);

        for (const n of this.newsUnseen) {
          if (!this.images[n.id]) {
            this.getImage(n.id);
          } else {
            n.imagePresent = true;
            n.image = this.images[n.id];
          }
        }
      }
    );
  }


  private loadSeenNews() {

    this.newsService.getSeenNews(this.pageSeen).subscribe(
      data => {
        this.news = data['content'];
        this.newsNum = data['totalElements'];
        this.pagesSeen = new Array(data['totalPages']);

        for (const n of this.news) {
          if (!this.images[n.id]) {
            this.getImage(n.id);
          } else {
            n.imagePresent = true;
            n.image = this.images[n.id];
          }
        }
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
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error;
    }
  }

  private clearForm() {
    this.newsForm.reset();
    this.submitted = false;
    this.isImageSaved = false;
  }



  refresh(): void {
    window.location.reload();
  }

  vanishAlert() {
    this.successCreated = false;
    this.router.navigate(['/news']);
  }

  setSeen() {
  this.seen = true;
  this.unseen = false;
  }

  setUnseen() {
  this.unseen = true;
  this.seen = false;
  }

  setPageSeen(i, event: any) {
    event.preventDefault();
    this.pageSeen = i;
    if (i > this.paginationLimitUpSeen) {
      this.paginationLimitUpSeen += 15;
      this.paginationLimitDownSeen += 15;
    }
    if (i < this.paginationLimitDownSeen) {
      this.paginationLimitUpSeen -= 15;
      this.paginationLimitDownSeen -= 15;
    }
    this.loadSeenNews();
  }

  setPageUnseen(i, event: any) {
    event.preventDefault();
    this.pageUnseen = i;
    if (i > this.paginationLimitUpUnseen) {
      this.paginationLimitUpUnseen += 15;
      this.paginationLimitDownUnseen += 15;
    }
    if (i < this.paginationLimitDownUnseen) {
      this.paginationLimitUpUnseen -= 15;
      this.paginationLimitDownUnseen -= 15;
    }
    this.loadUnseenNews();
  }
}
