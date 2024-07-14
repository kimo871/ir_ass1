// ======= intial =============================

const category = {
    name : "All" ,
    color : "black",
    id: 0
}

const category1 = {
    name : "Personal" ,
    color : "red",
    id: 1
}

const category2 = {
    name : "Work" ,
    color : "green",
    id: 2
}

const category3 = {
    name : "Gym" ,
    color : "blue",
    id: 3
}


// =============== Categroies ============================



let Categories = [category,category1,category2,category3];


let str = JSON.stringify(Categories);

console.log(str)


if(localStorage.getItem("Categories")===null) localStorage.setItem("Categories",JSON.stringify(Categories));



// =============== Tasks ============================

const add_task_db = (task) =>{
     
  if(localStorage.getItem("Tasks")===null) localStorage.setItem("Tasks",JSON.stringify([]));

  else {
     let tasks = JSON.parse(localStorage.getItem("Tasks"));
     console.log(tasks)
     tasks.push(task);
     localStorage.setItem("Tasks",JSON.stringify(tasks));
  }

}


// =====================================================================================

const add_list_db = (task) =>{
     
    if(localStorage.getItem("Categories")===null) localStorage.setItem("Categories",JSON.stringify([]));
  
    else {
       let tasks = JSON.parse(localStorage.getItem("Categories"));
       console.log(tasks)
       tasks.push(task);
       localStorage.setItem("Categories",JSON.stringify(tasks));
    }
  
  }

const get_Tasks = ()=>{
    let results =  JSON.parse(localStorage.getItem("Tasks"))
    return results;
}

const get_Categories = ()=>{
let results =  JSON.parse(localStorage.getItem("Categories"))
return results;
}

export {get_Categories,get_Tasks,add_task_db,add_list_db}