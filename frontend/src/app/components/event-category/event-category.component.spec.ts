import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EventCategoryComponent } from './event-category.component';

describe('EventCategoryComponent', () => {
  let component: EventCategoryComponent;
  let fixture: ComponentFixture<EventCategoryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EventCategoryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EventCategoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
