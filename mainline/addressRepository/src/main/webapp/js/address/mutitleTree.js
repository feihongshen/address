 		var zTreeObj1;
        var zTreeObj2;
       
        var demosetting = {
            edit: {
                enable: false,
                showRemoveBtn: false,
                showRenameBtn: false
            },
            data: {
                simpleData: {
                    enable: true
                }
            },
            callback: {
                //onClick : menuOnClick
            }
        };
        function menuOnClick(event, treeId, treeNode, clickFlag) {

        }
        //注册toSource函数，解决ie不支持Array的toSource()方法的问题
        Array.prototype.toSource = function (){
             var str = "[";
             for(var i = 0 ;i<this.length;i++){
                 str+="{id:\""+this[i].id+
                         "\",pId:\""+this[i].pId
                         +"\",name:\""+this[i].name
                         +"\",isParent:\""+this[i].isParent
                         +"\",file:\""+this[i].file
                         //+"\",icon:\""+this[i].icon
                         +"\",open:\""+this[i].open
                         +"\"},";
             }
             if(this.length != 0){
                 str = str.substring(0, str.length-1);
             }
             str +="]";
            return str;
        } ;
        //注册unique函数，去掉array中重复的对象（id相同即为同一对象）
        Array.prototype.unique = function (){
             var r = new Array();
            label:for(var i = 0, n = this.length; i < n; i++) {
                for(var x = 0, y = r.length; x < y; x++) {
                    if(r[x].id == this[i].id) {
                        continue label;
                    }
                }
                r[r.length] = this[i];
            }
            return r;
        } ;
        

        //查找被移动节点的所有父节点
        function findParentNodes(treeNode, parentNodes){
            parentNodes += "{id:"+treeNode.id+",pId:"+treeNode.pId+
            ",name:\""+treeNode.name+"\",open:"+treeNode.open+"},";
            if(treeNode != null && treeNode.getParentNode()!= null){
                parentNodes =findParentNodes(treeNode.getParentNode(),parentNodes);

            }
            return parentNodes;
        }        
        //移动节点
        function moveNodes(zTreeFrom,treeNode,zTreeTo,divStrFrom,divStrTo){
            /////////////////////////////////treeNode的所有父节点
            var parentNodes ="[";
            if(treeNode.pId != null){
                parentNodes = findParentNodes(treeNode,parentNodes);
                parentNodes = parentNodes.substring(0,parentNodes.length-1);
            }

            parentNodes +="]";
            //alert(parentNodes);
            var parentNodesArray = eval(parentNodes);
            ///////////////////////////////
            var nodes = "[";
            nodes+= "{id:"+treeNode.id+",pId:"+treeNode.pId+",name:\""+treeNode.name+"\",open:"+treeNode.open+"},";
            nodes = operChildrenNodes(treeNode,nodes);
            nodes = nodes.substring(0,nodes.length-1);
            nodes+="]";
            var nodesArray = eval(nodes);
            var divFromArray = eval(divStrFrom);
            var divToArray = eval(divStrTo);
            for(var i = 0;i<nodesArray.length;i++){//删除节点
                for(var j = 0;j<divFromArray.length;j++){
                    if(divFromArray[j].id == nodesArray[i].id){
                        divFromArray.splice(j,1);
                    }
                }
            }

            divToArray = divToArray.concat(nodesArray);//增加节点
            divToArray = divToArray.concat(parentNodesArray);

            ///////////////////////////////////////////////////////////////////////////////////////去重复
            divFromArray = divFromArray.unique();
            divToArray = divToArray.unique();
            ///////////////////////////////////////////////////////////////////////////////////////////去重复

            if(zTreeFrom.setting.treeId == "sourceStationtree"){
                leftDivStr = divFromArray.toSource();
                rightDivStr =divToArray.toSource();
                $.fn.zTree.init($("#sourceStationtree"), demosetting, divFromArray);
                $.fn.zTree.init($("#targetStationtree"), demosetting,divToArray);

            }else{
                leftDivStr = divToArray.toSource();
                rightDivStr =divFromArray.toSource();
                $.fn.zTree.init($("#targetStationtree"), demosetting, divFromArray);
                $.fn.zTree.init($("#sourceStationtree"), demosetting,divToArray);
            }
        }

         
        //查找指定节点下的所有子节点
        function operChildrenNodes(treeNode,nodes){
            if(treeNode.children!= undefined){//是父节点，有子节点
                for(var j = 0;j<treeNode.children.length;j++){
                    var childNode = treeNode.children[j];
                    nodes+="{id:"+childNode.id+",pId:"+childNode.pId+",name:\""+childNode.name+"\",open:"+childNode.open+"},";
                    nodes = operChildrenNodes(childNode,nodes);
                }
            }else{//没子节点
            }
            return nodes;
        }

        
        $(document).ready(function(){
            
           
            $(function() {
                $("#toRight").click(function() {
                    moveNodes(zTreeObj1,zTreeObj1.getSelectedNodes()[0],zTreeObj2,leftDivStr,rightDivStr);
                });
                $("#toLeft").click(function(){
                    moveNodes(zTreeObj2,zTreeObj2.getSelectedNodes()[0],zTreeObj1,rightDivStr,leftDivStr);

                });    
            });
        });