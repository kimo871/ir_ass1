
import {add_task_db, add_list_db , get_Categories,get_Tasks} from './store.js'



document.addEventListener("DOMContentLoaded",()=>{
  
  load_tasks_dom();
})



let icon_plus = document.querySelectorAll("i.fa-plus");

let btn_list = document.querySelector(".btn-list")

let icon_close = document.querySelectorAll("i.fa-close")

let pop_container = document.querySelector(".pop-container");

let add_task = document.querySelector(".add-task");

let add_list = document.querySelector(".add-list");


let toggle_popups = (form)=>{
    pop_container.classList.toggle("container-observer");
    pop_container.classList.toggle("op-75");
    form.classList.toggle("container-observer");
    form.classList.toggle("op-1");
};

icon_plus.forEach((item)=>{
  item.addEventListener("click",()=> toggle_popups(add_task));
});

btn_list.addEventListener("click",()=> toggle_popups(add_list));


icon_close.forEach((item)=>{
  item.addEventListener("click",(e)=> {
    pop_container.classList.toggle("container-observer");
    pop_container.classList.toggle("op-75");
    e.target.parentNode.parentNode.classList.toggle("container-observer");
    e.target.parentNode.parentNode.classList.toggle("op-1");
  });
})

// =============================================================================================

let side_nav = document.querySelector(".side-nav");

console.log(get_Categories())

get_Categories().map((item)=>{
    //logger
    console.log(item)

    let li_item = document.createElement("li");

    let i_item = document.createElement("i");

    let span_item = document.createElement("span")

    i_item.classList.add("fa-solid","fa-circle");

    i_item.style.color=`${item.color}`

    span_item.textContent =item.name;

    li_item.append(i_item);

    li_item.append(span_item);

    side_nav.appendChild(li_item);
})


// ============================ Tasks Manipulation to dom ==============================================

let tasks_container = document.querySelector(".tasks-container");

let empty_container = document.querySelector(".empty-container");

let task_data = {id:1,title:"Task 1",description:"study for exam !",category:2};

const add_task_form = document.querySelector(".add-task form");

add_task_form.onsubmit = (e)=>{
  e.preventDefault();
  const form_data = new FormData(e.target);

  let task = {};

  for(let [key,value] of form_data.entries()){

    // loggger
    console.log(key,value);

    task[key]=value;
  }

  console.log(task);

  Add_task(task);

}

const Add_task = (task)=>{
    add_task_db(task);
    location.reload();
}

const load_tasks_dom = ()=>{

    if(get_Tasks().length>0)  empty_container.style="display:none";
    
    get_Tasks().forEach((item)=>{

        const task = document.createElement("div");

        task.classList.add("task");
        
        const checkbox_container = document.createElement("div");
        
        checkbox_container.classList.add("checkbox-container");
        
        const checkbox = document.createElement("input");
         
        checkbox.type="checkbox";
        
        checkbox_container.append(checkbox);
        
        const task_details = document.createElement("div");
        
        task_details.classList.add("task-details");
        
        const title = document.createElement("h3");
        
        title.textContent = item.title;
    
        
        const description = document.createElement("h5");
        
        description.textContent = item.description;
        
        
        const i_item2 = document.createElement("i");
        
        i_item2.classList.add("fa-solid","fa-circle");
    
        task_details.append(title,description,i_item2,document.createTextNode("personal"));
    
        const task_actions = document.createElement("div");
    
        task_actions.classList.add("task-actions");
    
        const i_item_pen = document.createElement("i");
    
        i_item_pen.classList.add("fa-solid","fa-pen-to-square");
    
        const i_item_trash = document.createElement("i");
    
        i_item_trash.classList.add("fa-solid","fa-trash");
    
        task_actions.append(i_item_pen,i_item_trash);
    
        task.append(checkbox_container,task_details,task_actions);
    
        tasks_container.append(task)

    })

    
}



const add_list_form = document.querySelector(".add-list form");

add_list_form.onsubmit = (e)=>{
    e.preventDefault();
    const form_data = new FormData(e.target);
  
    let list = {};
  
    for(let [key,value] of form_data.entries()){
  
      // loggger
      console.log(key,value);
  
      list[key]=value;
    }
  
    console.log(list);
  
    Add_List(list);
  }


const Add_List = ()=>{
   add_list_db();
   location.reload()
}















