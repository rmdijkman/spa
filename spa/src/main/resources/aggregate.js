if (typeof(taskFrequency) == "undefined"){
  taskFrequency = {};
  taskDuration = {};
  function stringToKey(s){
    return (s==null)?"undefined":s.replace(/[^a-zA-Z0-9]/g,'');
  }
  colors = ["#15517F", "#76C3FF", "#2AA2FF", "#556D7F", "#2181CC", "#153E7F", "#9EC3FF", "#2A7BFF", "#55657F", "#2163CC"];
  function toRGBA(RGB,A){
    return 'rgba('+parseInt(RGB.substring(1,3),16)+','+parseInt(RGB.substring(3,5),16)+','+parseInt(RGB.substring(5,7),16)+','+A+')';
  }
}

for (i = 0; i < stream.length; i++){
  key = stringToKey(stream[i].c2);

  if (typeof(taskFrequency[key]) == "undefined"){
    taskFrequency[key] = {};
  }
  taskFrequency[key].value = (typeof(taskFrequency[key].value)=="undefined")?1:taskFrequency[key].value+1;
  taskFrequency[key].label = stream[i].c2;
  taskFrequency[key].color = colors[Object.keys(taskFrequency).indexOf(key)%10];

  if (typeof(taskDuration[key] == "undefined")){
    taskDuration[key] = {};
  }
  taskDuration[key].count = (typeof(taskDuration[key].count)=="undefined")?1:taskDuration[key].count+1;
  taskDuration[key].total = (typeof(taskDuration[key].total)=="undefined")?0:taskDuration[key].total;
  taskDuration[key].total += (Date.parse(stream[i].c4) - Date.parse(stream[i].c3))/(1000*60*60*24);
  taskDuration[key].value = taskDuration[key].total/taskDuration[key].count;
  taskDuration[key].label = stream[i].c2;
};

keys = Object.keys(taskDuration);
labels = [];
data = [];
color = colors[0];
for (i = 0; i < keys.length; i++){
  labels.push(taskDuration[Object.keys(taskDuration)[i]].label);
  data.push(taskDuration[Object.keys(taskDuration)[i]].value);
}
dataset = {
  label: "Duration (days)",
  fillColor: toRGBA(color,0.5),
  strokeColor: toRGBA(color,0.8),
  data: data
};

stream = [];