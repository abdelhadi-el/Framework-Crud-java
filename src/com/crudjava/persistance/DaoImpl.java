package com.crudjava.persistance;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;

public class DaoImpl<T, typeId> implements Dao<T, typeId>{

	public T instance ;
	//ArrayList<T> objects = new ArrayList<>();
	public ArrayList<String> attributes = new ArrayList<>();
	public ArrayList<String> values = new ArrayList<>();
	public ArrayList<String> attributesTypes = new ArrayList<>();
	public String tableName;
	
	public DaoImpl(T t) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException {
		super();
		getInfos(t);
		
		instance = (T) t.getClass().getDeclaredConstructor().newInstance();
 
	}

	@SuppressWarnings("unused")
	public  void getInfos(T classe) throws IllegalArgumentException, IllegalAccessException {
        Field[] fs = classe.getClass().getDeclaredFields();
        String str ;
        for (Field field : fs) {
			attributes.add(field.getName());
			str = field.getType().toString();
			int indexOflastPoint  = str.lastIndexOf(".");
        	str = str.substring(indexOflastPoint + 1, str.length()) ;
			attributesTypes.add(str);
			if (field.get(classe) != null) {
			//	System.out.println("not null getinfosfct");
				values.add(  field.get(classe).toString() );
			}

		}
        tableName = classe.getClass().getSimpleName();
		
	}
	
	public String typesDataBase(String attributeType ) {
		String type = "";
		if( attributeType.equals("String") ) {
			type = "VARCHAR(255)" ;
		}else if ( attributeType.equals("Integer") || attributeType.equals("int") ) {
			type = "Integer(10)" ;
		}else if ( attributeType.equals("Float") ) {
			type = "FLOAT(10)" ;
		}else if ( attributeType.equals("Boolean") ) {
			type = "BOOLEAN" ;
		}else if ( attributeType.equals("Date") ) {
			type = "DATE" ;
		}
		return type ;
	}
	
    public Boolean createTable() {
    	//System.out.println("table called");
        try {
        	
           Connection connection =  DatabaseConnection.getInstance().getConnection();
          //  Connection connection = ConnexionBD.getInstance().getConnexion();

            Statement statement = null;

            statement = connection.createStatement();

            String query = "CREATE TABLE IF NOT EXISTS " + tableName + " (";
            int count = 0 ;
            for (String attribute : attributes) {
            	if (count == 0) {
					query += attribute + " " +typesDataBase(attributesTypes.get(count)) +" AUTO_INCREMENT " +  ", " ;  
	                count++ ;
				}else {
					if (attributes.indexOf(attribute) < attributes.size() - 1) {
	                    query += attribute + " " + typesDataBase(attributesTypes.get(count)) + ", ";
	                } else {
	                    query += attribute + " " + typesDataBase(attributesTypes.get(count)) + ", PRIMARY KEY ( " + attributes.get(0) + " ));";
	                }
				}
                
            }

            statement.executeUpdate(query);
         //   connection.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

	@Override
	public Boolean save(T object) {
			
			this.values = new ArrayList<>() ;
			this.attributes = new ArrayList<>() ;
			this.attributesTypes = new ArrayList<>() ;
			try {
				getInfos(object);
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			createTable() ; // creation de table s'il n'existe pas 
		try {

			Connection connection =  DatabaseConnection.getInstance().getConnection();
           // Connection connection = ConnexionBD.getInstance().getConnexion();

            java.sql.PreparedStatement statement = null;
            int helper = 0 ;
            String query = "INSERT INTO " +  tableName + " (";
            for (String attribute : attributes) {
            	if (helper == 0) {
					helper++ ;
				}else {
					if (attributes.indexOf(attribute) < attributes.size() - 1) {
		                query += attribute + ",";
	                } else {
	                    query += attribute + ")";
	                }
				}
            }
            helper = 0 ;
            query += " VALUES(";
            for (String attribute : attributes) {
            	if (helper == 0) {
					helper++ ;
				}else {
					if (attributes.indexOf(attribute) < attributes.size() - 1) {
		                query += "?,";
	                } else {
	                    query += "?);";
	                }
				}
            }
            statement = connection.prepareStatement(query);

            for (int i = 1; i < attributesTypes.size(); i++) {
            	//System.out.println(attributesTypes.get(i-1));
            	if( attributesTypes.get(i).equals("String")) {
                    statement.setString(i, values.get(i));
            	}else if( attributesTypes.get(i).equals("Float")) {
                    statement.setFloat(i, Float.parseFloat( values.get(i)));
            	}else if( attributesTypes.get(i).equals("Integer") || attributesTypes.get(i).equals("int") ) {
                    statement.setInt(i, Integer.parseInt( values.get(i)));
            	}else if( attributesTypes.get(i).equals("Date")  ) {
                    statement.setDate(i, Date.valueOf( values.get(i)));
            	}else if( attributesTypes.get(i).equals("Boolean")  ) {
                    statement.setBoolean(i, Boolean.parseBoolean( values.get(i)));
            	}


            }

            statement.execute();
           // connection.close();
         
	}catch (Exception e) {
        e.printStackTrace();
        return false ;
    }
        return true ;

	}

	@Override
	public Optional<T> getObject(typeId id) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		//T instance ;
		Field[] fields = instance.getClass().getDeclaredFields() ;
 		try {
              Connection connection =  DatabaseConnection.getInstance().getConnection();
           // Connection connection = ConnexionBD.getInstance().getConnexion();

 			Statement statement = null;
            //subject = new Subject();
            String query = "select * from " + tableName + " where " + attributes.get(0) + "='" + id + "';";

            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                for (int i = 1; i <= attributesTypes.size(); i++) {
                	if( attributesTypes.get(i-1).equals("String")) {
                		
                		fields[i-1].set(instance, resultSet.getString(i));
                	
                	}else if( attributesTypes.get(i-1).equals("Float")) {
                		fields[i-1].setFloat(instance, resultSet.getFloat(i));
                	}else if( attributesTypes.get(i-1).equals("Integer") || attributesTypes.get(i-1).equals("int") ) {
                		fields[i-1].set(instance, resultSet.getInt(i));
                	}else if( attributesTypes.get(i-1).equals("Date")  ) {
                		fields[i+1].set(instance, resultSet.getDate(i));
                	}else if( attributesTypes.get(i-1).equals("Boolean")  ) {
                		fields[i+1].setBoolean(instance, resultSet.getBoolean(i));
                	}
                }
             //   connection.close();
            } else {
             //   connection.close();
               // return null;
            }


        } catch (Exception e) {
            e.printStackTrace();
           // return null;
        }
        return Optional.ofNullable(instance);
	}

	@Override
	public Boolean deleteObject(typeId id) {
		try {
			
	        Connection connection =  DatabaseConnection.getInstance().getConnection();
			//Connection connection = ConnexionBD.getInstance().getConnexion();
            //PreparedStatement preparedStatement = null;
            Statement statement = connection.createStatement();

            String query = "delete from " + tableName + " where " + attributes.get(0) + " = '" + id + "';";
           // preparedStatement = connection.prepareStatement(query);

            if (id != null) {
                statement.execute(query);
            }
         //   connection.close();
           // return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }		
		return true;
	}

	@Override
	public Boolean updateObject(typeId id, T newObject) {
      
		this.values = new ArrayList<>() ;
		this.attributes = new ArrayList<>() ;
		this.attributesTypes = new ArrayList<>() ;
		try {
			getInfos(newObject);
		} catch (IllegalArgumentException | IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 try {
	            Connection connection =  DatabaseConnection.getInstance().getConnection();
	           // Connection connection = ConnexionBD.getInstance().getConnexion();
	            Statement statement = null;
	            System.out.println(attributes.get(0) + '-');
	            String query = "UPDATE " + tableName + " SET ";
	            for (int i = 1; i < attributes.size(); i++) {
	                if (i < values.size() - 1) {
	                    query += attributes.get(i) + "='" + values.get(i) + "', ";
	                } else {
	                    query += attributes.get(i) + "='" + values.get(i) + "' WHERE " + attributes.get(0) + "=" + id + ";";
	                }
	            }

	            if (id != null) {
	                statement = connection.createStatement();
	                statement.execute(query);
	            }
	         //   connection.close();
	            return true;

	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	}

	@Override
	public ArrayList<T> getAll() {
		try {
			Field[] fields = instance.getClass().getDeclaredFields() ;

	        Connection connection =  DatabaseConnection.getInstance().getConnection();
            //Connection connection = ConnexionBD.getInstance().getConnexion();
            Statement statement = null;
            ArrayList<T> allSubjects = new ArrayList<>();
            String query = "select * from " + tableName + ";";

            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int index = 0 ;
           // getInfos(instance);
  //          T tableTemp  = null;
            while (resultSet.next()) {

//            	System.out.println(index);
//            	System.out.println(attributes + "   attributes");
               // Object subject 
                Class<T> iiClass = (Class<T>) instance.getClass() ;
                T subject = iiClass.getDeclaredConstructor().newInstance() ;
    			fields = subject.getClass().getDeclaredFields() ;
               // System.out.println(subject.getClass().getDeclaredFields());
                for (int i = 1; i <= attributes.size(); i++) {
                //	System.out.println(i);
                    if( attributesTypes.get(i-1).equals("String")) {
                    	
                		fields[i-1].set(subject, resultSet.getString(i));
                		//values.add(resultSet.getString(i)) ;
                		
                	}else if( attributesTypes.get(i-1).equals("Float")) {
                		
                		fields[i-1].setFloat(subject, resultSet.getFloat(i));
                		values.add(Float.toString(resultSet.getInt(i))) ;
                		
                	}else if( attributesTypes.get(i-1).equals("Integer") || attributesTypes.get(i-1).equals("int") ) {

                		fields[i-1].set(subject, resultSet.getInt(i));
                	//	values.add(Integer.toString(resultSet.getInt(i))) ;
                		
                	}else if( attributesTypes.get(i-1).equals("Date")  ) {
                		
                		fields[i+1].set(subject, resultSet.getDate(i));
                		values.add((resultSet.getDate(i)).toString()) ;
                		
                	}else if( attributesTypes.get(i-1).equals("Boolean")  ) {
                		
                		fields[i+1].setBoolean(subject, resultSet.getBoolean(i));
                		values.add(Boolean.toString(resultSet.getBoolean(i))) ;
                	}
                //    System.out.println(instance.toString());

                }
            	System.out.println(values + "   values");
//            	System.out.println(subject.toString() + "   instance");
//                System.out.println(allSubjects + "	aazazazaza  avant");

                allSubjects.add(index,subject);
              //  handleGetAll(allSubjects.get(index));
//                if (index == 0) {
//                	System.out.println("heerepppppp");
//                	tableTemp = allSubjects.get(index) ; 
//				}
//                System.out.println(tableTemp + "tableT");
//
//                System.out.println(allSubjects + "	aazazazaza apres");
            	this.values = new ArrayList<>() ;
    			this.attributes = new ArrayList<>() ;
    			this.attributesTypes = new ArrayList<>() ;
                getInfos(subject);


                
           //     instance.getClass().getDeclaredConstructor().newInstance() ;
            	index++ ;
            }
//            System.out.println(objects + " onbject");
           // System.out.println(allSubjects + " aalll");
          //  connection.close();
            return allSubjects;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
	public String formatString(String str) {
        return "`" + str + "`";
    }
//	public void handleGetAll( T  object) {
//		objects.add(object) ;
//	}
}
