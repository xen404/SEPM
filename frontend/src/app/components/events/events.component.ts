import {ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {EventService} from '../../services/event.service';
import {EventSimple} from '../../dtos/eventsimple';
import {AuthService} from '../../services/auth.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {NewsService} from '../../services/news.service';
import {NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import {ActivatedRoute} from '@angular/router';
import {EventDetail} from '../../dtos/event-detail';
import * as _ from 'lodash';
import {DomSanitizer} from '@angular/platform-browser';

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit {

  @ViewChild('closeModal') closeModal: ElementRef;
  error: boolean = false;
  errorMessage: string = '';
  eventForm: FormGroup;
  submitted: boolean = false;
  private events: EventDetail[];
  selectedFile: File = null;
  public imagePath;
  imgURL: any;
  private id: number;
  images: string[] = [];
  private path: string;
  private eventAdded: boolean;

  imageError: string;
  isImageSaved: boolean;
  cardImageBase64: string;
  allowedTypes = ['image/png', 'image/jpeg'];

  successCreated: boolean = false;
  successCreatedMessage: string = '';

  // events: Event[];
  simpleEventAllList: EventSimple[];
  simpleEventList: EventSimple[];
  categories: String[];
  idCategory: number;
  page: number = 0;
  pageSize: number = 15;
  paginationLimitUp: number = 15;
  paginationLimitDown: number = 0;
  public searchEvents: Event[] = [];
  pages: Array<number>;
  totalElements: number;
  search: boolean = false;
  title: string;
  start: boolean = false;
  category: string = '';
  duration: string;
  public isCollapsed = true;

  constructor(private eventService: EventService, private newsService: NewsService, private ngbPaginationConfig: NgbPaginationConfig,
              private formBuilder: FormBuilder, private cd: ChangeDetectorRef,
              private authService: AuthService, private route: ActivatedRoute,
              private sanitizer: DomSanitizer) {
    this.eventForm = this.formBuilder.group({
      title: ['', [Validators.required]],
      category: ['', [Validators.required]],
      duration: ['', [Validators.required]],
      description: ['', [Validators.required]],
      image: ['', [Validators.required]],
    });
  }


  ngOnInit(): void {
    this.getEventCategories();
    this.loadEventPagination();
    this.idCategory = this.eventService.getCategoryId(this.route.snapshot.paramMap.get('category'));
    this.category = this.eventService.getCategoryString(this.idCategory);
  }
  /**
   * gets all categories
   */
  private getEventCategories(): void {
    this.eventService.getEventCategories().subscribe(categories => this.categories = categories.map(c => c.toLowerCase()));
  }

  /**
   * get all categorys by id.
   * @param idCategory id of the category
   */
  private findAllSimpleEventsByCategory(idCategory: number): void {
    this.eventService.findAllSimpleEventsByCategory(idCategory).subscribe(simpleEventList => this.simpleEventList = simpleEventList);
  }

  /**
   * load all events from BE.
   */
  private findAllSimpleEvents() {
    this.eventService.getAllSimpleEvents().subscribe(simpleEventAllList => this.simpleEventAllList = simpleEventAllList);
  }

  /**
   * Loads all the events from the BE with pagination.
   */
  private loadEventPagination() {
    this.search = false;
    this.eventService.getEventPagination(this.page).subscribe(
      data => {
        this.searchEvents = data['content'];
        this.pages = new Array(data['totalPages']);
        this.totalElements = data['totalElements'];
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * searches all events with given params.
   * @param title of event
   * @param category of event
   * @param duration of event
   */
  private searchEvent(title: string, category: string, duration: string) {
    this.vanishError();
    this.start = false;
    this.title = title;
    this.category = category;
    this.duration = duration;
    this.search = true;

    if ( title === '%') {
      title = '//%';
    }
    if ( category === '%') {
      category = '//%';
    }
    if ( duration === '%') {
      duration = '//%';
    }
    if ( title === '_') {
      title = '//_';
    }
    if ( category === '_') {
      category = '//_';
    }
    if ( duration === '_') {
      duration = '//_';
    }

    if (title === '') {
      title = '%';
    }
    if (duration === '') {
      duration = '-1';
    }

    if (isNaN(Number(duration))) {
      duration = '';
    }

    this.eventService.searchEvent(title, this.eventService.getCategoryId(category), Number(duration), this.page).subscribe(data => {
        this.searchEvents = data['content'];
        this.pages = new Array(data['totalPages']);
        this.totalElements = data['totalElements'];

      }, error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * sets the page for pagination
   * @param i current page
   * @param event prevent default
   */
  setPage(i, event: any) {
    event.preventDefault();
    this.page = i;
    if (i > this.paginationLimitUp) {
      this.paginationLimitUp += 15;
      this.paginationLimitDown += 15;
    }
    if (i < this.paginationLimitDown) {
      this.paginationLimitUp -= 15;
      this.paginationLimitDown -= 15;
    }
    if (this.search === false) {
      this.loadEventPagination();
    } else {
      this.searchEvent(this.title, this.category, this.duration);
    }
  }

  /**
   * error function.
   * @param error input
   */
  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error;
    }
  }

  /**
   * clears the from.
   */
  private clearFormFilter() {
    this.eventForm.controls['duration'].reset();
    this.eventForm.controls['category'].reset();
    this.submitted = false;
  }



  addEvent() {
    this.submitted = true;
    if (this.eventForm.valid) {
      const event: EventDetail = new EventDetail(null,
        this.eventForm.controls.title.value,
        this.eventForm.controls.category.value,
        this.eventForm.controls.description.value,
        this.eventForm.controls.duration.value,
        null,
        null,
        '10:00',
        '11:00',
        null,
        null,
        null,
        false,
      );
      this.createEvent(event);
      this.clearForm();
      this.path = event.category.toLowerCase();
    } else {
      console.log('Invalid input');
    }
  }

  private clearForm() {
    this.eventForm.reset();
    this.submitted = false;
    this.isImageSaved = false;
  }

  createEvent(eventPost: EventDetail) {
    this.eventService.createEvent(eventPost).subscribe(
      (msg: EventDetail) => {
        this.id = msg.id;
        this.closeModal.nativeElement.click();
        this.successCreated = true;
        this.successCreatedMessage = 'You have successfully created the event: ' + msg.title;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      },

      () => {
        this.renderImage();
      }
    );

  }

  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  /**
   * Save image to database
   */
  renderImage() {
    this.eventService.getLastImage().subscribe(
      (ns) => {
        if (this.selectedFile != null) {
          const fd = new FormData();
          fd.append('file', this.selectedFile, this.selectedFile.name);
          this.eventService.uploadImage(ns, fd).subscribe(res => {
            console.log(res);
          });
        }
      },
      error => {
        this.defaultServiceErrorHandling(error);
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
          this.eventForm.controls.image.setValue(this.cardImageBase64);
          this.imgURL = reader.result;
        };
      };
      reader.readAsDataURL(this.selectedFile);
    }
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  vanishAlert() {
    this.successCreated = false;
    this.successCreatedMessage = '';
  }
}

