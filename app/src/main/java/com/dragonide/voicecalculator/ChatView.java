package com.dragonide.voicecalculator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;




public class ChatView extends RelativeLayout {

    private static final int FLAT = 0;
    private static final int ELEVATED = 1;


    private ListView chatListView;



    private boolean previousFocusState = false, useEditorAction, isTyping;


    private TypingListener typingListener;
    private OnSentMessageListener onSentMessageListener;
    private ChatViewListAdapter chatViewListAdapter;

    private int inputFrameBackgroundColor, backgroundColor;


    private float bubbleElevation;

    private int bubbleBackgroundRcv, bubbleBackgroundSend; // Drawables cause cardRadius issues. Better to use background color

    private TypedArray attributes, textAppearanceAttributes;
    private Context context;



     ChatView(Context context) {
        this(context, null);
    }

    public ChatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(getContext()).inflate(R.layout.chat_view, this, true);
        this.context = context;
        initializeViews();
        getXMLAttributes(attrs, defStyleAttr);
        setViewAttributes();
        setListAdapter();

    }

    private void initializeViews() {
        chatListView = findViewById(R.id.chat_list);



    }

    private void getXMLAttributes(AttributeSet attrs, int defStyleAttr) {
        attributes = context.obtainStyledAttributes(attrs, R.styleable.ChatView, defStyleAttr, R.style.ChatViewDefault);
        getChatViewBackgroundColor();
        getAttributesForBubbles();
        getAttributesForInputFrame();

        getUseEditorAction();
        attributes.recycle();
    }

    private void setListAdapter() {
        chatViewListAdapter = new ChatViewListAdapter(context);
        chatListView.setAdapter(chatViewListAdapter);
    }


    private void setViewAttributes() {
        setChatViewBackground();

       // setInputTextAttributes();

        setUseEditorAction();
    }

    private void getChatViewBackgroundColor() {
        backgroundColor =Color.TRANSPARENT;//attributes.getColor(R.styleable.ChatView_backgroundColor, -1);
    }

    private void getAttributesForBubbles() {

        float dip4 = context.getResources().getDisplayMetrics().density * 4.0f;
        int elevation = attributes.getInt(R.styleable.ChatView_bubbleElevation, ELEVATED);
        bubbleElevation = 0;//elevation == ELEVATED ? dip4 : 0;

        bubbleBackgroundRcv = 0;//attributes.getColor(R.styleable.ChatView_bubbleBackgroundRcv, ContextCompat.getColor(context, R.color.default_bubble_color_rcv));
        bubbleBackgroundSend = 0;//attributes.getColor(R.styleable.ChatView_bubbleBackgroundSend, ContextCompat.getColor(context, R.color.default_bubble_color_send));
    }


    private void getAttributesForInputFrame() {
        inputFrameBackgroundColor = 0;//attributes.getColor(R.styleable.ChatView_inputBackgroundColor, -1);
    }


    private void setChatViewBackground() {
        this.setBackgroundColor(backgroundColor);
    }



    private void setTextAppearanceAttributes() {
        final int textAppearanceId = attributes.getResourceId(R.styleable.ChatView_inputTextAppearance, 0);
        textAppearanceAttributes = getContext().obtainStyledAttributes(textAppearanceId, R.styleable.ChatViewInputTextAppearance);
    }






    private void getUseEditorAction() {
        useEditorAction = attributes.getBoolean(R.styleable.ChatView_inputUseEditorAction, false);
    }

    private void setUseEditorAction() {
        if (useEditorAction) {

        } else {
            }
    }

    private boolean hasStyleResourceSet() {
        return attributes.hasValue(R.styleable.ChatView_inputTextAppearance);
    }












    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params) {
        return super.addViewInLayout(child, index, params);
    }





    public void setOnSentMessageListener(OnSentMessageListener onSentMessageListener) {
        this.onSentMessageListener = onSentMessageListener;
    }

    private void sendMessage(String message, long stamp, String exeTime) {

        ChatMessage chatMessage = new ChatMessage(message, stamp, ChatMessage.Type.SENT, exeTime);
        if (onSentMessageListener != null && onSentMessageListener.sendMessage(chatMessage)) {
            chatViewListAdapter.addMessage(chatMessage);
           // inputEditText.setText("");
        }
    }

    public void addMessage(ChatMessage chatMessage) {
        chatViewListAdapter.addMessage(chatMessage);
    }

    public void addMessages(ArrayList<ChatMessage> messages) {
        chatViewListAdapter.addMessages(messages);
    }

    public void removeMessage(int position) {
        chatViewListAdapter.removeMessage(position);
    }

    public void clearMessages() {
        chatViewListAdapter.clearMessages();
    }





    public interface TypingListener {

        void userStartedTyping();

        void userStoppedTyping();

    }

    public interface OnSentMessageListener {
        boolean sendMessage(ChatMessage chatMessage);
    }

    private class ChatViewListAdapter extends BaseAdapter {

        public final int STATUS_SENT = 0;
        public final int STATUS_RECEIVED = 1;

        ArrayList<ChatMessage> chatMessages;

        Context context;
        LayoutInflater inflater;

        public ChatViewListAdapter(Context context) {
            this.chatMessages = new ArrayList<>();
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return chatMessages.size();
        }

        @Override
        public Object getItem(int position) {
            return chatMessages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return chatMessages.get(position).getType().ordinal();
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            int type = getItemViewType(position);
            if (convertView == null) {
                switch (type) {
                    case STATUS_SENT:
                        convertView = inflater.inflate(R.layout.chat_item_sent, parent, false);
                        break;
                    case STATUS_RECEIVED:
                        convertView = inflater.inflate(R.layout.chat_item_rcv, parent, false);
                        break;
                }

                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.getMessageTextView().setText(chatMessages.get(position).getMessage());
            holder.getTimestampTextView().setText(chatMessages.get(position).getFormattedTime());
            holder.getExecutionTime().setText(chatMessages.get(position).getProcessTime() + "ms");
            holder.getChatBubble().setCardElevation(bubbleElevation);
            holder.setBackground(type);

            return convertView;
        }

        private void addMessage(ChatMessage message) {
            chatMessages.add(message);
            notifyDataSetChanged();
        }

        private void addMessages(ArrayList<ChatMessage> chatMessages) {
            this.chatMessages.addAll(chatMessages);
            notifyDataSetChanged();
        }

        private void removeMessage(int position) {
            if (this.chatMessages.size() > position) {
                this.chatMessages.remove(position);
            }
        }

        private void clearMessages() {
            this.chatMessages.clear();
            notifyDataSetChanged();
        }

        class ViewHolder {
            View row;
            CardView bubble;
            TextView messageTextView;
            TextView timestampTextView;
            TextView executionTime;

            private ViewHolder(View convertView) {
                row = convertView;
                bubble = convertView.findViewById(R.id.bubble);
            }

            private TextView getMessageTextView() {
                if (messageTextView == null) {
                    messageTextView = row.findViewById(R.id.message_text_view);
                }
                return messageTextView;
            }

            private TextView getTimestampTextView() {
                if (timestampTextView == null) {
                    timestampTextView = row.findViewById(R.id.timestamp_text_view);
                }

                return timestampTextView;
            }

            public TextView getExecutionTime() {
                if (executionTime == null) {
                    executionTime = row.findViewById(R.id.time_for_processing);
                }
                return executionTime;
            }

            private CardView getChatBubble() {
                if (bubble == null) {
                    bubble = row.findViewById(R.id.bubble);
                }

                return bubble;
            }

            private void setBackground(int messageType) {

                int background = ContextCompat.getColor(context, android.R.color.transparent);

                switch (messageType) {
                    case STATUS_RECEIVED:
                        background = bubbleBackgroundRcv;
                        break;
                    case STATUS_SENT:
                        background = bubbleBackgroundSend;
                        break;
                }

                bubble.setCardBackgroundColor(background);
            }
        }
    }
}
