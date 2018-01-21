 package the.instinctives.studentgroups;

 import android.content.ActivityNotFoundException;
 import android.content.Context;
 import android.content.Intent;
 import android.graphics.Color;
 import android.net.Uri;
 import android.os.Environment;
 import android.text.Html;
 import android.util.DisplayMetrics;
 import android.util.Log;
 import android.view.Gravity;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.View.OnClickListener;
 import android.view.ViewGroup;
 import android.widget.BaseAdapter;
 import android.widget.LinearLayout;
 import android.widget.LinearLayout.LayoutParams;
 import android.widget.TextView;
 import java.io.File;
 import java.util.ArrayList;

 public class ChatAdapter extends BaseAdapter
 {
     private Context mContext;
     private ArrayList<Messages> mMessages;
     private int dwidth;
     private int dheight;
     Database db;

     public ChatAdapter(Context context, ArrayList<Messages> messages)
     {
         super();
         db=new Database(context);
         this.mContext = context;
         this.mMessages = messages;

         DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
         dwidth = displayMetrics.widthPixels;
         dheight = displayMetrics.heightPixels;
     }

     @Override
     public int getCount()
     {
         return mMessages.size();
     }

     @Override
     public Object getItem(int position)
     {
         return mMessages.get(position);
     }

     @Override
     public View getView(int position, View convertView, ViewGroup parent) {
         final Messages message = (Messages) this.getItem(position);

         ViewHolder holder;
         if(convertView == null)
         {
             holder = new ViewHolder();
             convertView = LayoutInflater.from(mContext).inflate(R.layout.sms_row, parent, false);
             holder.message = (TextView) convertView.findViewById(R.id.message_text);
             holder.layout3=(LinearLayout) convertView.findViewById(R.id.layout3);
             holder.layout2=(LinearLayout) convertView.findViewById(R.id.layout2);
             convertView.setTag(holder);
             holder.message.setTextColor(Color.BLACK);
             holder.name= (TextView) convertView.findViewById(R.id.textView);
         }
         else
         {
             holder = (ViewHolder) convertView.getTag();
             convertView = LayoutInflater.from(mContext).inflate(R.layout.sms_row, parent, false);
             holder.message = (TextView) convertView.findViewById(R.id.message_text);
             holder.layout3=(LinearLayout) convertView.findViewById(R.id.layout3);
             holder.layout2=(LinearLayout) convertView.findViewById(R.id.layout2);
             convertView.setTag(holder);
             holder.message.setTextColor(Color.BLACK);
             holder.name= (TextView) convertView.findViewById(R.id.textView);
         }

         holder.name.setText(message.getMsgtime());
         holder.message.setText(message.getMessage());

         new Font().overrideFonts(mContext, holder.name);

         holder.name.setTextColor(mContext.getResources().getColor(R.color.colorWhite));

         holder.message.setTextSize(15);

         new Font().overrideFonts(mContext, holder.message);

         holder.message.setTextColor(Color.WHITE);

         LayoutParams lp = (LayoutParams) holder.layout3.getLayoutParams();
         LayoutParams lp1 = (LayoutParams) holder.name.getLayoutParams();
         LayoutParams lp2 = (LayoutParams) holder.message.getLayoutParams();

         if(message.getDirection()==0)
         {
             holder.layout3.setBackgroundResource(R.drawable.bubble2);
             lp.gravity = Gravity.RIGHT;
             lp1.gravity= Gravity.RIGHT;
             lp1.setMargins(0,0,10,0);
             lp2.setMargins(0,0,10,0);
             holder.name.setVisibility(View.GONE);
         }

         else
         {
             db.open();
             db.updatemsg(message.getMessageid(), "3");
             db.close();

             holder.layout3.setBackgroundResource(R.drawable.bubble1);
             lp.gravity = Gravity.LEFT;
             lp1.gravity = Gravity.LEFT;
             lp1.setMargins(10, 0, 0, 0);
             lp2.setMargins(10, 0, 0, 0);
         }
         holder.message.setMaxWidth((int) (dwidth*0.75));


         return convertView;
     }

     private static class ViewHolder
     {
         TextView message;
         LinearLayout layout3,layout2;
         TextView name;
     }

     @Override
     public long getItemId(int position) {
         // TODO Auto-generated method stub
         return 0;
     }
 }
