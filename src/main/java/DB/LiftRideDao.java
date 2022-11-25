//package DB;
//
//import Shapes.LiftRideEvent;
//
//import org.apache.commons.dbcp2.BasicDataSource;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//
//public class LiftRideDao {
//    private static BasicDataSource dataSource;
//    public  LiftRideDao(){
//        dataSource = DBCPDataSource.getDataSource();
//    }
//
//    public void createLiftRideEvent(LiftRideEvent liftRideEvent){
//        Connection conn = null;
//        PreparedStatement preparedStatement = null;
//        String insertQueryStatement = "INSERT INTO LiftRides (skierId, resortId, seasonId, dayId, time, liftId) " +
//                "VALUES (?,?,?,?,?,?)";
//
//        try{
//            conn = dataSource.getConnection();
//            preparedStatement = conn.prepareStatement(insertQueryStatement);
//            preparedStatement.setInt(1, liftRideEvent.getSkierID());
//            preparedStatement.setInt(2, liftRideEvent.getResortID());
//            preparedStatement.setInt(3, Integer.parseInt(liftRideEvent.getSeasonID()));
//            preparedStatement.setInt(4, Integer.parseInt(liftRideEvent.getDayID()));
//            preparedStatement.setInt(5, liftRideEvent.getLiftTime());
//            preparedStatement.setInt(6, liftRideEvent.getLiftID());
//
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try{
//                if(conn!=null){
//                    conn.close();
//                }
//                if(preparedStatement!=null){
//                    preparedStatement.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//}
